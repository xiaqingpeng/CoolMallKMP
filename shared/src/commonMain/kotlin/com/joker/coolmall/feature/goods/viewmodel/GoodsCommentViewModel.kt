package com.joker.coolmallkmp.feature.goods.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.GoodsRepository
import com.joker.coolmallkmp.core.model.entity.Comment
import com.joker.coolmallkmp.core.model.request.GoodsCommentPageRequest
import com.joker.coolmallkmp.core.ui.UiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface GoodsCommentUiState {
    data object Loading : GoodsCommentUiState
    data class Success(val comments: List<Comment>, val hasMore: Boolean) : GoodsCommentUiState
    data class Error(val message: UiMessage) : GoodsCommentUiState
}

class GoodsCommentViewModel(
    private val goodsId: Long,
    private val repository: GoodsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<GoodsCommentUiState>(GoodsCommentUiState.Loading)
    val uiState: StateFlow<GoodsCommentUiState> = _uiState.asStateFlow()
    private val comments = mutableListOf<Comment>()
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
        comments.clear()
        loadPage(refresh = true)
    }

    fun loadMore() {
        if (!hasMore || loadingMore || _uiState.value is GoodsCommentUiState.Loading) return
        page += 1
        loadPage(refresh = false)
    }

    private fun loadPage(refresh: Boolean) {
        viewModelScope.launch {
            loadingMore = !refresh
            if (refresh) _uiState.value = GoodsCommentUiState.Loading
            runCatching {
                repository.getGoodsCommentPage(
                    GoodsCommentPageRequest(goodsId = goodsId.toString(), page = page, size = pageSize),
                )
            }.onSuccess { response ->
                loadingMore = false
                if (!response.isSucceeded) {
                    if (refresh) {
                        _uiState.value = GoodsCommentUiState.Error(
                            UiMessage.loadFailed(response.message, response.code),
                        )
                    } else {
                        page = (page - 1).coerceAtLeast(1)
                    }
                    return@onSuccess
                }
                val pageData = response.data
                val newComments = pageData?.list.orEmpty()
                comments += newComments
                val total = pageData?.pagination?.total
                hasMore = total?.let { comments.size < it } ?: (newComments.size >= pageSize)
                _uiState.value = GoodsCommentUiState.Success(comments.toList(), hasMore)
            }.onFailure { error ->
                loadingMore = false
                if (refresh) {
                    _uiState.value = GoodsCommentUiState.Error(UiMessage.network(error.message))
                } else {
                    page = (page - 1).coerceAtLeast(1)
                }
            }
        }
    }
}
