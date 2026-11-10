package com.joker.coolmall.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.OrderRepository
import com.joker.coolmall.core.model.entity.Logistics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage

sealed interface OrderLogisticsUiState {
    data object Loading : OrderLogisticsUiState
    data class Success(val logistics: Logistics?) : OrderLogisticsUiState
    data class Error(val message: UiMessage) : OrderLogisticsUiState
}

class OrderLogisticsViewModel(
    private val orderId: Long,
    private val orderRepository: OrderRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderLogisticsUiState>(OrderLogisticsUiState.Loading)
    val uiState: StateFlow<OrderLogisticsUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = OrderLogisticsUiState.Loading
            runCatching {
                orderRepository.getOrderLogistics(orderId)
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    _uiState.value = OrderLogisticsUiState.Success(response.data)
                } else {
                    _uiState.value = OrderLogisticsUiState.Error(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                _uiState.value = OrderLogisticsUiState.Error(UiMessage.network(error.message))
            }
        }
    }
}
