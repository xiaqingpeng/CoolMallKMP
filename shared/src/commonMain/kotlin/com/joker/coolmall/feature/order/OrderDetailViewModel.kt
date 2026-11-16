package com.joker.coolmallkmp.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.OrderRepository
import com.joker.coolmallkmp.core.model.entity.Order
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage

sealed interface OrderDetailUiState {
    data object Loading : OrderDetailUiState
    data class Success(val order: Order) : OrderDetailUiState
    data class Error(val message: UiMessage) : OrderDetailUiState
}

class OrderDetailViewModel(
    private val orderId: Long,
    private val orderRepository: OrderRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderDetailUiState>(OrderDetailUiState.Loading)
    val uiState: StateFlow<OrderDetailUiState> = _uiState.asStateFlow()

    private val _isActing = MutableStateFlow(false)
    val isActing: StateFlow<Boolean> = _isActing.asStateFlow()

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = OrderDetailUiState.Loading
            runCatching {
                orderRepository.getOrderInfo(orderId)
            }.onSuccess { response ->
                if (response.isSucceeded && response.data != null) {
                    _uiState.value = OrderDetailUiState.Success(response.data)
                } else {
                    _uiState.value = OrderDetailUiState.Error(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                _uiState.value = OrderDetailUiState.Error(UiMessage.network(error.message))
            }
        }
    }

    fun cancelOrder(remark: String? = null) {
        if (_isActing.value) return
        viewModelScope.launch {
            _isActing.value = true
            runCatching {
                orderRepository.cancelOrder(orderId, remark ?: "用户取消")
            }.onSuccess { response ->
                _isActing.value = false
                if (response.isSucceeded) {
                    _message.emit(UiMessage.Code(MessageCode.OrderCancelled))
                    load()
                } else {
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.OrderCancelFailed, response.code.toString()))
                }
            }.onFailure { error ->
                _isActing.value = false
                _message.emit(UiMessage.network(error.message))
            }
        }
    }

    fun confirmReceive() {
        if (_isActing.value) return
        viewModelScope.launch {
            _isActing.value = true
            runCatching {
                orderRepository.confirmReceive(orderId)
            }.onSuccess { response ->
                _isActing.value = false
                if (response.isSucceeded) {
                    _message.emit(UiMessage.Code(MessageCode.OrderConfirmedReceive))
                    load()
                } else {
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.OrderConfirmFailed, response.code.toString()))
                }
            }.onFailure { error ->
                _isActing.value = false
                _message.emit(UiMessage.network(error.message))
            }
        }
    }
}
