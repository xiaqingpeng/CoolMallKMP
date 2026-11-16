package com.joker.coolmall.core.common.base.state

sealed interface BaseNetWorkListUiState : BaseUiState<Nothing> {
    data object Loading : BaseNetWorkListUiState
    data object Success : BaseNetWorkListUiState
    data object Error : BaseNetWorkListUiState
    data object Empty : BaseNetWorkListUiState
}
