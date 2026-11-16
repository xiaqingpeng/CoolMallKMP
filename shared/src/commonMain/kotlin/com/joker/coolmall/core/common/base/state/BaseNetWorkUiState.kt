package com.joker.coolmallkmp.core.common.base.state

sealed interface BaseNetWorkUiState<out T> : BaseUiState<T> {
    data object Loading : BaseNetWorkUiState<Nothing>
    data class Success<T>(val data: T) : BaseNetWorkUiState<T>
    data class Error(
        val message: String? = null,
        val exception: Throwable? = null,
    ) : BaseNetWorkUiState<Nothing>
}
