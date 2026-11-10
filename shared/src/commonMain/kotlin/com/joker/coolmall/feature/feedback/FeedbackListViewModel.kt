package com.joker.coolmall.feature.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.FeedbackRepository
import com.joker.coolmall.core.model.entity.Feedback
import com.joker.coolmall.core.model.request.PageRequest
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
) : ViewModel() {

    private val _uiState = MutableStateFlow<FeedbackListUiState>(FeedbackListUiState.Loading)
    val uiState: StateFlow<FeedbackListUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = FeedbackListUiState.Loading
            runCatching {
                feedbackRepository.getFeedbackPage(PageRequest(page = 1, size = 50))
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    _uiState.value = FeedbackListUiState.Success(
                        response.data?.list.orEmpty(),
                    )
                } else {
                    _uiState.value = FeedbackListUiState.Error(
                        UiMessage.loadFailed(response.message, response.code),
                    )
                }
            }.onFailure { error ->
                _uiState.value = FeedbackListUiState.Error(UiMessage.network(error.message))
            }
        }
    }
}
