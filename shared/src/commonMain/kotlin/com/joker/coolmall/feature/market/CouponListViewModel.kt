package com.joker.coolmallkmp.feature.market

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.CouponRepository
import com.joker.coolmallkmp.core.model.entity.Coupon
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage

sealed interface CouponListUiState {
    data object Loading : CouponListUiState
    data class Success(val coupons: List<Coupon>) : CouponListUiState
    data class Error(val message: UiMessage) : CouponListUiState
}

class CouponListViewModel(
    private val couponRepository: CouponRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CouponListUiState>(CouponListUiState.Loading)
    val uiState: StateFlow<CouponListUiState> = _uiState.asStateFlow()

    private val _message = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val message: SharedFlow<String> = _message.asSharedFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = CouponListUiState.Loading
            runCatching {
                couponRepository.getUserCouponList()
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    _uiState.value = CouponListUiState.Success(response.data.orEmpty())
                } else {
                    // list 失败时尝试 page
                    loadPageFallback(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                loadPageFallback(UiMessage.network(error.message))
            }
        }
    }

    private suspend fun loadPageFallback(fallbackMessage: UiMessage) {
        runCatching {
            couponRepository.getUserCouponPage(page = 1, size = 50)
        }.onSuccess { response ->
            if (response.isSucceeded) {
                _uiState.value = CouponListUiState.Success(
                    response.data?.list.orEmpty()
                )
            } else {
                _uiState.value = CouponListUiState.Error(
                    UiMessage.loadFailed(response.message, response.code).takeIf {
                        response.message?.isNotBlank() == true
                    } ?: fallbackMessage,
                )
            }
        }.onFailure {
            _uiState.value = CouponListUiState.Error(fallbackMessage)
        }
    }
}
