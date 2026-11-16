package com.joker.coolmall.feature.feedback

import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.common.base.state.BaseNetWorkListUiState
import com.joker.coolmall.core.common.base.state.LoadMoreState
import com.joker.coolmall.core.common.base.viewmodel.BaseNetWorkListViewModel
import com.joker.coolmall.core.data.repository.FeedbackRepository
import com.joker.coolmall.core.data.repository.CommonRepository
import com.joker.coolmall.core.data.repository.AuthRepository
import com.joker.coolmall.core.model.entity.DictItem
import com.joker.coolmall.core.model.entity.Feedback
import com.joker.coolmall.core.model.request.PageRequest
import com.joker.coolmall.core.session.AuthSession
import com.joker.coolmall.core.session.networkAuthLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage

sealed interface FeedbackListUiState {
    data object Loading : FeedbackListUiState
    data class Success(val list: List<Feedback>) : FeedbackListUiState
    data class Error(val message: UiMessage) : FeedbackListUiState
}

class FeedbackListViewModel(
    private val feedbackRepository: FeedbackRepository,
    private val commonRepository: CommonRepository,
    private val authRepository: AuthRepository,
) : BaseNetWorkListViewModel<Feedback>() {

    val feedbackUiState: StateFlow<FeedbackListUiState> = combine(
        networkUiState,
        listData,
        errorMessage,
    ) { state, list, error ->
        when (state) {
            BaseNetWorkListUiState.Loading -> FeedbackListUiState.Loading
            BaseNetWorkListUiState.Success,
            BaseNetWorkListUiState.Empty -> FeedbackListUiState.Success(list)
            BaseNetWorkListUiState.Error -> FeedbackListUiState.Error(
                error ?: UiMessage.network(null),
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FeedbackListUiState.Loading,
    )
    val uiState: StateFlow<FeedbackListUiState> = feedbackUiState
    private val _feedbackTypes = MutableStateFlow<List<DictItem>>(emptyList())
    val feedbackTypes: StateFlow<List<DictItem>> = _feedbackTypes.asStateFlow()
    val isLoadingMore: StateFlow<Boolean> = loadMoreState
        .map { it == LoadMoreState.Loading }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        load()
        loadFeedbackTypes()
    }

    override suspend fun requestListData(
        page: Int,
        size: Int,
    ) = run {
        if (AuthSession.token.isBlank()) {
            networkAuthLog("feedback list token empty, restoring session")
            authRepository.restoreSession()
        }
        networkAuthLog("feedback list load token=${AuthSession.tokenSummary()}")
        feedbackRepository.getFeedbackPage(PageRequest(page = page, size = size))
    }

    private fun loadFeedbackTypes() {
        viewModelScope.launch {
            runCatching { commonRepository.getFeedbackTypes() }
                .onSuccess { response ->
                    if (response.isSucceeded) {
                        _feedbackTypes.value = response.data.orEmpty()
                    }
                }
        }
    }
}
