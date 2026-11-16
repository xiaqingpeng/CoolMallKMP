package com.joker.coolmall.core.common.base.viewmodel

import com.joker.coolmall.core.common.base.state.BaseNetWorkUiState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 统一网络请求状态机。子类只需要实现 suspend requestApi。
 */
abstract class BaseNetWorkViewModel<T> : BaseViewModel() {
    private val _uiState = MutableStateFlow<BaseNetWorkUiState<T>>(
        BaseNetWorkUiState.Loading,
    )
    val uiState: StateFlow<BaseNetWorkUiState<T>> = _uiState.asStateFlow()

    protected abstract suspend fun requestApi(): T

    fun executeRequest() {
        viewModelScope.launch {
            _uiState.value = BaseNetWorkUiState.Loading
            runCatching { requestApi() }
                .onSuccess { _uiState.value = BaseNetWorkUiState.Success(it) }
                .onFailure {
                    _uiState.value = BaseNetWorkUiState.Error(it.message, it)
                }
        }
    }

    fun retryRequest() = executeRequest()
}
