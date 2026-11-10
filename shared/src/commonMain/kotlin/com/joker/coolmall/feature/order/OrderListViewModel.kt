package com.joker.coolmall.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.OrderRepository
import com.joker.coolmall.core.model.entity.Order
import com.joker.coolmall.core.model.request.OrderPageRequest
import com.joker.coolmall.feature.order.model.OrderStatus
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage

sealed interface OrderListUiState {
    data object Loading : OrderListUiState
    data class Success(
        val orders: List<Order>,
        val hasMore: Boolean,
    ) : OrderListUiState

    data class Error(val message: UiMessage) : OrderListUiState
}

/**
 * 订单列表：按 Tab 状态分页（对齐 CoolMallKotlin，简化为单 Tab 状态机 + 切换时懒加载）
 */
class OrderListViewModel(
    private val orderRepository: OrderRepository,
    initialTabIndex: Int = 0,
) : ViewModel() {

    private val _selectedTabIndex = MutableStateFlow(
        initialTabIndex.coerceIn(0, OrderStatus.entries.lastIndex)
    )
    val selectedTabIndex: StateFlow<Int> = _selectedTabIndex.asStateFlow()

    private val tabStates = OrderStatus.entries.map {
        MutableStateFlow<OrderListUiState>(OrderListUiState.Loading)
    }
    private val tabUiStates: List<StateFlow<OrderListUiState>> = tabStates.map { it.asStateFlow() }
    private val tabPages = MutableList(OrderStatus.entries.size) { 1 }
    private val tabHasMore = MutableList(OrderStatus.entries.size) { true }
    private val tabLoaded = MutableList(OrderStatus.entries.size) { false }
    private val tabOrders = OrderStatus.entries.map { mutableListOf<Order>() }

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    private val pageSize = 10

    init {
        loadTab(_selectedTabIndex.value, force = true)
    }

    fun uiStateFor(tabIndex: Int): StateFlow<OrderListUiState> = tabUiStates[tabIndex]

    fun selectTab(index: Int) {
        if (index !in OrderStatus.entries.indices) return
        _selectedTabIndex.value = index
        if (!tabLoaded[index]) {
            loadTab(index, force = true)
        }
    }

    fun refresh(tabIndex: Int = _selectedTabIndex.value) {
        loadTab(tabIndex, force = true)
    }

    fun loadMore(tabIndex: Int = _selectedTabIndex.value) {
        if (tabIndex !in OrderStatus.entries.indices) return
        if (!tabHasMore[tabIndex] || _isLoadingMore.value) return
        if (tabStates[tabIndex].value is OrderListUiState.Loading) return
        loadTab(tabIndex, force = false)
    }

    fun cancelOrder(orderId: Long, remark: String? = null) {
        viewModelScope.launch {
            runCatching {
                orderRepository.cancelOrder(orderId, remark ?: "用户取消")
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    _message.emit(UiMessage.Code(MessageCode.OrderCancelled))
                    refresh()
                } else {
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.OrderCancelFailed, response.code.toString()))
                }
            }.onFailure { error ->
                _message.emit(UiMessage.network(error.message))
            }
        }
    }

    fun confirmReceive(orderId: Long) {
        viewModelScope.launch {
            runCatching {
                orderRepository.confirmReceive(orderId)
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    _message.emit(UiMessage.Code(MessageCode.OrderConfirmedReceive))
                    refresh()
                } else {
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.OrderConfirmFailed, response.code.toString()))
                }
            }.onFailure { error ->
                _message.emit(UiMessage.network(error.message))
            }
        }
    }

    private fun loadTab(tabIndex: Int, force: Boolean) {
        viewModelScope.launch {
            val isRefresh = force || !tabLoaded[tabIndex]
            if (isRefresh) {
                tabPages[tabIndex] = 1
                tabHasMore[tabIndex] = true
                tabOrders[tabIndex].clear()
                tabStates[tabIndex].value = OrderListUiState.Loading
            } else {
                if (!tabHasMore[tabIndex]) return@launch
                tabPages[tabIndex] += 1
                _isLoadingMore.value = true
            }

            val status = OrderStatus.entries[tabIndex]
            val page = tabPages[tabIndex]
            runCatching {
                orderRepository.getOrderPage(
                    OrderPageRequest(
                        page = page,
                        size = pageSize,
                        status = status.status,
                    )
                )
            }.onSuccess { response ->
                _isLoadingMore.value = false
                if (!response.isSucceeded) {
                    if (isRefresh) {
                        tabStates[tabIndex].value = OrderListUiState.Error(UiMessage.loadFailed(response.message, response.code))
                    } else {
                        tabPages[tabIndex] = (page - 1).coerceAtLeast(1)
                    }
                    return@onSuccess
                }

                val pageData = response.data
                val list = pageData?.list.orEmpty()
                if (isRefresh) {
                    tabOrders[tabIndex].clear()
                }
                tabOrders[tabIndex].addAll(list)

                val total = pageData?.pagination?.total
                tabHasMore[tabIndex] = if (total != null) {
                    tabOrders[tabIndex].size < total
                } else {
                    list.size >= pageSize
                }
                tabLoaded[tabIndex] = true
                tabStates[tabIndex].value = OrderListUiState.Success(
                    orders = tabOrders[tabIndex].toList(),
                    hasMore = tabHasMore[tabIndex],
                )
            }.onFailure { error ->
                _isLoadingMore.value = false
                if (isRefresh) {
                    tabStates[tabIndex].value =
                        OrderListUiState.Error(UiMessage.network(error.message))
                } else {
                    tabPages[tabIndex] = (page - 1).coerceAtLeast(1)
                }
            }
        }
    }
}
