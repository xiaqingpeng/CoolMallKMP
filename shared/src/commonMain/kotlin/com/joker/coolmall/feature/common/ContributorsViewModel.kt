package com.joker.coolmallkmp.feature.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.UserContributorRepository
import com.joker.coolmallkmp.core.model.entity.UserContributor
import com.joker.coolmallkmp.core.model.request.PageRequest
import com.joker.coolmallkmp.core.ui.UiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ContributorsUiState {
    data object Loading : ContributorsUiState
    data class Success(
        val items: List<UserContributor>,
        val hasMore: Boolean,
    ) : ContributorsUiState
    data class Error(val message: UiMessage) : ContributorsUiState
}

class ContributorsViewModel(
    private val repository: UserContributorRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<ContributorsUiState>(ContributorsUiState.Loading)
    val uiState: StateFlow<ContributorsUiState> = _uiState.asStateFlow()

    private val items = mutableListOf<UserContributor>()
    private var page = 1
    private val pageSize = 20
    private var hasMore = true
    private var loadingMore = false

    init {
        refresh()
    }

    fun refresh() {
        page = 1
        hasMore = true
        items.clear()
        loadPage(refresh = true)
    }

    fun loadMore() {
        if (!hasMore || loadingMore || _uiState.value is ContributorsUiState.Loading) return
        page += 1
        loadPage(refresh = false)
    }

    private fun loadPage(refresh: Boolean) {
        viewModelScope.launch {
            loadingMore = !refresh
            if (refresh) _uiState.value = ContributorsUiState.Loading
            runCatching {
                repository.getUserContributorPage(PageRequest(page = page, size = pageSize))
            }.onSuccess { response ->
                loadingMore = false
                if (!response.isSucceeded) {
                    if (refresh) {
                        _uiState.value = ContributorsUiState.Error(
                            UiMessage.loadFailed(response.message, response.code),
                        )
                    } else {
                        page = (page - 1).coerceAtLeast(1)
                    }
                    return@onSuccess
                }
                val pageData = response.data
                val newItems = pageData?.list.orEmpty()
                items += newItems
                val total = pageData?.pagination?.total
                hasMore = total?.let { items.size < it } ?: (newItems.size >= pageSize)
                _uiState.value = ContributorsUiState.Success(items.toList(), hasMore)
            }.onFailure { error ->
                loadingMore = false
                if (refresh) {
                    _uiState.value = ContributorsUiState.Error(UiMessage.network(error.message))
                } else {
                    page = (page - 1).coerceAtLeast(1)
                }
            }
        }
    }
}
