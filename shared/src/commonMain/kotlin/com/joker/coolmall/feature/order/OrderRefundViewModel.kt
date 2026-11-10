package com.joker.coolmall.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.CommonRepository
import com.joker.coolmall.core.data.repository.OrderRepository
import com.joker.coolmall.core.model.entity.DictItem
import com.joker.coolmall.core.model.entity.Order
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage

sealed interface OrderRefundUiState {
    data object Loading : OrderRefundUiState
    data class Success(val order: Order) : OrderRefundUiState
    data class Error(val message: UiMessage) : OrderRefundUiState
}

sealed interface RefundReasonsUiState {
    data object Loading : RefundReasonsUiState
    data class Success(val reasons: List<DictItem>) : RefundReasonsUiState
    data class Error(val message: UiMessage) : RefundReasonsUiState
}

/**
 * 退款申请：加载订单 + 字典退款原因 + 提交
 */
class OrderRefundViewModel(
    private val orderId: Long,
    private val orderRepository: OrderRepository,
    private val commonRepository: CommonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderRefundUiState>(OrderRefundUiState.Loading)
    val uiState: StateFlow<OrderRefundUiState> = _uiState.asStateFlow()

    private val _reasonsState =
        MutableStateFlow<RefundReasonsUiState>(RefundReasonsUiState.Loading)
    val reasonsState: StateFlow<RefundReasonsUiState> = _reasonsState.asStateFlow()

    private val _selectedReason = MutableStateFlow<DictItem?>(null)
    val selectedReason: StateFlow<DictItem?> = _selectedReason.asStateFlow()

    private val _customReason = MutableStateFlow("")
    val customReason: StateFlow<String> = _customReason.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    private val _submitted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val submitted: SharedFlow<Unit> = _submitted.asSharedFlow()

    init {
        loadOrder()
        loadReasons()
    }

    fun loadOrder() {
        viewModelScope.launch {
            _uiState.value = OrderRefundUiState.Loading
            runCatching {
                orderRepository.getOrderInfo(orderId)
            }.onSuccess { response ->
                if (response.isSucceeded && response.data != null) {
                    _uiState.value = OrderRefundUiState.Success(response.data)
                } else {
                    _uiState.value = OrderRefundUiState.Error(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                _uiState.value = OrderRefundUiState.Error(UiMessage.network(error.message))
            }
        }
    }

    fun loadReasons() {
        viewModelScope.launch {
            _reasonsState.value = RefundReasonsUiState.Loading
            runCatching {
                commonRepository.getOrderRefundReasons()
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    val list = response.data.orEmpty()
                    _reasonsState.value = RefundReasonsUiState.Success(list)
                    // 字典失败时退回空列表，仍可手动填写
                    if (list.isNotEmpty() && _selectedReason.value == null) {
                        _selectedReason.value = list.first()
                    }
                } else {
                    _reasonsState.value = RefundReasonsUiState.Error(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                _reasonsState.value =
                    RefundReasonsUiState.Error(UiMessage.network(error.message))
            }
        }
    }

    fun selectReason(item: DictItem) {
        _selectedReason.value = item
    }

    fun updateCustomReason(value: String) {
        _customReason.value = value
    }

    /**
     * 提交退款：优先用字典选中项 name，否则用自定义文本。
     */
    fun submit() {
        if (_isSubmitting.value) return
        val reason = _selectedReason.value?.name?.takeIf { it.isNotBlank() }
            ?: _customReason.value.trim().takeIf { it.isNotBlank() }
        if (reason == null) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.OrderRefundNeedReason)) }
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            runCatching {
                orderRepository.refundOrder(orderId, reason)
            }.onSuccess { response ->
                _isSubmitting.value = false
                if (response.isSucceeded) {
                    _message.emit(UiMessage.Code(MessageCode.OrderRefundSuccess))
                    _submitted.emit(Unit)
                } else {
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.OrderRefundSubmitFailed, response.code.toString()))
                }
            }.onFailure { error ->
                _isSubmitting.value = false
                _message.emit(UiMessage.network(error.message))
            }
        }
    }
}
