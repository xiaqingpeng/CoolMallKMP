package com.joker.coolmall.feature.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedbackListUiState>(FeedbackListUiState.Loading)
    val uiState: StateFlow<FeedbackListUiState> = _uiState.asStateFlow()
    private val _feedbackTypes = MutableStateFlow<List<DictItem>>(emptyList())
    val feedbackTypes: StateFlow<List<DictItem>> = _feedbackTypes.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()
    private var currentPage = 1
    private var totalCount: Int? = null

    init {
        load()
        loadFeedbackTypes()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = FeedbackListUiState.Loading
            fetchPage(page = 1, append = false)
        }
    }

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchPage(page = 1, append = false)
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        val state = _uiState.value as? FeedbackListUiState.Success ?: return
        if (_isLoadingMore.value || !hasMore(state.list.size)) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            fetchPage(page = currentPage + 1, append = true)
            _isLoadingMore.value = false
        }
    }

    fun hasMore(currentSize: Int): Boolean =
        totalCount == null || currentSize < (totalCount ?: 0)

    private suspend fun fetchPage(page: Int, append: Boolean) {
        runCatching {
                if (AuthSession.token.isBlank()) {
                    networkAuthLog("feedback list token empty, restoring session")
                    authRepository.restoreSession()
                }
                networkAuthLog("feedback list load token=${AuthSession.tokenSummary()}")
                feedbackRepository.getFeedbackPage(PageRequest(page = page, size = 10))
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    currentPage = page
                    totalCount = response.data?.pagination?.total
                    val incoming = response.data?.list.orEmpty()
                    val existing = (_uiState.value as? FeedbackListUiState.Success)?.list.orEmpty()
                    _uiState.value = FeedbackListUiState.Success(
                        if (append) (existing + incoming).distinctBy { it.id } else incoming,
                    )
                } else {
                    if (!append) {
                        _uiState.value = FeedbackListUiState.Error(
                            UiMessage.loadFailed(response.message, response.code),
                        )
                    }
                }
            }.onFailure { error ->
                networkAuthLog(
                    "feedback list viewModel failure type=${error::class.simpleName} " +
                        "message=${error.message}",
                )
                if (!append) {
                    _uiState.value = FeedbackListUiState.Error(UiMessage.network(error.message))
                }
            }
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
