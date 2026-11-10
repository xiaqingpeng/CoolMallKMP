package com.joker.coolmall.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.OrderRepository
import com.joker.coolmall.core.platform.AlipayPayStatus
import com.joker.coolmall.core.platform.ThirdPartyCapabilities
import com.joker.coolmall.core.platform.payWithAlipay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 支付页事件。固定文案用 [ToastCode]，服务端/SDK 动态文案用 [ToastMessage]。
 */
sealed class OrderPayEvent {
    data class ToastCode(val code: PayToastCode, val arg: String? = null) : OrderPayEvent()
    data class ToastMessage(val message: String) : OrderPayEvent()
    data class Finished(val success: Boolean) : OrderPayEvent()
}

enum class PayToastCode {
    Unsupported,
    ParamsFailed,
    Success,
    Cancel,
    Failed,
    NetworkError,
}

class OrderPayViewModel(
    private val orderId: Long,
    private val orderRepository: OrderRepository,
) : ViewModel() {

    private val _isPaying = MutableStateFlow(false)
    val isPaying: StateFlow<Boolean> = _isPaying.asStateFlow()

    private val _events = MutableSharedFlow<OrderPayEvent>()
    val events: SharedFlow<OrderPayEvent> = _events.asSharedFlow()

    fun startAlipayPayment() {
        if (_isPaying.value) return
        if (!ThirdPartyCapabilities.supportsAlipay) {
            viewModelScope.launch {
                _events.emit(OrderPayEvent.ToastCode(PayToastCode.Unsupported))
            }
            return
        }
        viewModelScope.launch {
            _isPaying.value = true
            runCatching { orderRepository.alipayAppPay(orderId) }
                .onSuccess { resp ->
                    val orderStr = resp.data
                    if (!resp.isSucceeded || orderStr.isNullOrBlank()) {
                        val msg = resp.message?.takeIf { it.isNotBlank() }
                        if (msg != null) {
                            _events.emit(OrderPayEvent.ToastMessage(msg))
                        } else {
                            _events.emit(OrderPayEvent.ToastCode(PayToastCode.ParamsFailed))
                        }
                        _isPaying.value = false
                        return@onSuccess
                    }
                    val result = payWithAlipay(orderStr)
                    when (result.status) {
                        AlipayPayStatus.Success -> {
                            _events.emit(OrderPayEvent.ToastCode(PayToastCode.Success))
                            _events.emit(OrderPayEvent.Finished(success = true))
                        }
                        AlipayPayStatus.Cancel -> {
                            _events.emit(OrderPayEvent.ToastCode(PayToastCode.Cancel))
                            _events.emit(OrderPayEvent.Finished(success = false))
                        }
                        AlipayPayStatus.Unsupported -> {
                            if (result.memo.isNotBlank()) {
                                _events.emit(OrderPayEvent.ToastMessage(result.memo))
                            } else {
                                _events.emit(OrderPayEvent.ToastCode(PayToastCode.Unsupported))
                            }
                        }
                        AlipayPayStatus.Failed -> {
                            if (result.memo.isNotBlank()) {
                                _events.emit(OrderPayEvent.ToastMessage(result.memo))
                            } else {
                                _events.emit(
                                    OrderPayEvent.ToastCode(
                                        PayToastCode.Failed,
                                        arg = result.resultStatus,
                                    )
                                )
                            }
                            _events.emit(OrderPayEvent.Finished(success = false))
                        }
                    }
                    _isPaying.value = false
                }
                .onFailure { e ->
                    val msg = e.message?.takeIf { it.isNotBlank() }
                    if (msg != null) {
                        _events.emit(OrderPayEvent.ToastMessage(msg))
                    } else {
                        _events.emit(OrderPayEvent.ToastCode(PayToastCode.NetworkError))
                    }
                    _isPaying.value = false
                }
        }
    }
}
