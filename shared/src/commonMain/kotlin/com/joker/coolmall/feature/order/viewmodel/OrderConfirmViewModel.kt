package com.joker.coolmallkmp.feature.order.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.CartRepository
import com.joker.coolmallkmp.core.data.repository.OrderRepository
import com.joker.coolmallkmp.core.data.repository.PageRepository
import com.joker.coolmallkmp.core.model.entity.Address
import com.joker.coolmallkmp.core.model.entity.ConfirmOrder
import com.joker.coolmallkmp.core.model.entity.Order
import com.joker.coolmallkmp.core.model.request.CreateOrderRequest
import com.joker.coolmallkmp.core.order.CheckoutDraft
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage

sealed interface OrderConfirmUiState {
    data object Loading : OrderConfirmUiState
    data class Success(val page: ConfirmOrder) : OrderConfirmUiState
    data class Error(val message: UiMessage) : OrderConfirmUiState
}

class OrderConfirmViewModel(
    private val pageRepository: PageRepository,
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
) : ViewModel() {

    private val draft: CheckoutDraft.Snapshot = CheckoutDraft.consume()

    val carts = draft.displayCarts
    val totalAmount = draft.totalAmount
    val totalCount = draft.totalCount
    val isDraftEmpty = draft.isEmpty
    val fromCart = draft.fromCart

    private val _uiState = MutableStateFlow<OrderConfirmUiState>(OrderConfirmUiState.Loading)
    val uiState: StateFlow<OrderConfirmUiState> = _uiState.asStateFlow()

    private val _selectedAddress = MutableStateFlow<Address?>(null)
    val selectedAddress: StateFlow<Address?> = _selectedAddress.asStateFlow()

    private val _remark = MutableStateFlow("")
    val remark: StateFlow<String> = _remark.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _createdOrder = MutableSharedFlow<Order>(extraBufferCapacity = 1)
    val createdOrder: SharedFlow<Order> = _createdOrder.asSharedFlow()

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    init {
        loadConfirmPage()
    }

    fun loadConfirmPage() {
        viewModelScope.launch {
            _uiState.value = OrderConfirmUiState.Loading
            runCatching {
                pageRepository.getConfirmOrder()
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    val page = response.data ?: ConfirmOrder()
                    _uiState.value = OrderConfirmUiState.Success(page)
                    if (_selectedAddress.value == null) {
                        _selectedAddress.value = page.defaultAddress
                    }
                } else {
                    _uiState.value = OrderConfirmUiState.Error(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                _uiState.value = OrderConfirmUiState.Error(UiMessage.network(error.message))
            }
        }
    }

    fun updateRemark(value: String) {
        _remark.value = value
    }

    fun selectAddress(address: Address) {
        _selectedAddress.value = address
    }

    fun submitOrder() {
        if (_isSubmitting.value) return
        val address = _selectedAddress.value
        if (address == null || address.id <= 0) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.OrderConfirmNeedAddress)) }
            return
        }
        if (draft.goods.isEmpty()) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.OrderConfirmNoGoods)) }
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            val request = CreateOrderRequest(
                data = CreateOrderRequest.CreateOrder(
                    remark = _remark.value,
                    addressId = address.id,
                    goodsList = draft.goods,
                    title = "BUY_GOODS",  // resolved in UI if needed; API title
                ),
            )
            runCatching {
                orderRepository.createOrder(request)
            }.onSuccess { response ->
                if (response.isSucceeded && response.data != null) {
                    if (fromCart) {
                        draft.carts.forEach { cart ->
                            cart.spec.forEach { spec ->
                                cartRepository.removeSpec(cart.goodsId, spec.id)
                            }
                        }
                    }
                    _isSubmitting.value = false
                    _createdOrder.emit(response.data)
                } else {
                    _isSubmitting.value = false
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.OrderCreateFailed, response.code.toString()))
                }
            }.onFailure { error ->
                _isSubmitting.value = false
                _message.emit(UiMessage.network(error.message))
            }
        }
    }
}
