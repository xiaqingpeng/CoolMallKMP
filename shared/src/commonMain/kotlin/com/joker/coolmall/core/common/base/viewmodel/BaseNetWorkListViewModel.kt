package com.joker.coolmallkmp.core.common.base.viewmodel

import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.common.base.state.BaseNetWorkListUiState
import com.joker.coolmallkmp.core.common.base.state.LoadMoreState
import com.joker.coolmallkmp.core.model.response.NetworkPageData
import com.joker.coolmallkmp.core.model.response.NetworkResponse
import com.joker.coolmallkmp.core.ui.UiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 统一列表分页状态机。
 *
 * 页面可以通过继承此类共享首次加载、刷新、加载更多、空列表和错误状态，
 * 避免每个 Feature 重复实现页码和 hasMore 判断。
 */
abstract class BaseNetWorkListViewModel<T : Any> : BaseViewModel() {
    protected open val pageSize: Int = 10
    protected var currentPage: Int = 1
        private set

    private val _uiState = MutableStateFlow<BaseNetWorkListUiState>(
        BaseNetWorkListUiState.Loading,
    )
    val networkUiState: StateFlow<BaseNetWorkListUiState> = _uiState.asStateFlow()

    private val _listData = MutableStateFlow<List<T>>(emptyList())
    val listData: StateFlow<List<T>> = _listData.asStateFlow()

    private val _loadMoreState = MutableStateFlow(LoadMoreState.PullToLoad)
    val loadMoreState: StateFlow<LoadMoreState> = _loadMoreState.asStateFlow()

    private val _errorMessage = MutableStateFlow<UiMessage?>(null)
    val errorMessage: StateFlow<UiMessage?> = _errorMessage.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var hasMore = true
    private var requestInFlight = false

    protected abstract suspend fun requestListData(
        page: Int,
        size: Int,
    ): NetworkResponse<NetworkPageData<T>>

    fun load() {
        if (requestInFlight) return
        requestInFlight = true
        viewModelScope.launch {
            currentPage = 1
            _loadMoreState.value = LoadMoreState.Loading
            requestPage(append = false)
            requestInFlight = false
        }
    }

    fun refresh() {
        if (requestInFlight) return
        _isRefreshing.value = true
        viewModelScope.launch {
            currentPage = 1
            requestInFlight = true
            requestPage(append = false)
            requestInFlight = false
            _isRefreshing.value = false
        }
    }

    fun loadMore() {
        if (requestInFlight || !hasMore) return
        requestInFlight = true
        _loadMoreState.value = LoadMoreState.Loading
        viewModelScope.launch {
            requestPage(append = true)
            requestInFlight = false
        }
    }

    fun retryRequest() = load()

    fun shouldTriggerLoadMore(lastIndex: Int, totalCount: Int): Boolean =
        totalCount > 0 &&
            lastIndex >= totalCount - 3 &&
            _loadMoreState.value != LoadMoreState.Loading &&
            _loadMoreState.value != LoadMoreState.NoMore

    private suspend fun requestPage(append: Boolean) {
        runCatching { requestListData(currentPage, pageSize) }
            .onSuccess { response ->
                if (!response.isSucceeded) {
                    _errorMessage.value = UiMessage.loadFailed(response.message, response.code)
                    if (!append && _listData.value.isEmpty()) {
                        _uiState.value = BaseNetWorkListUiState.Error
                    }
                    _loadMoreState.value = LoadMoreState.Error
                    return@onSuccess
                }

                val incoming = response.data?.list.orEmpty()
                val pagination = response.data?.pagination
                val next = if (append) (_listData.value + incoming) else incoming
                _listData.value = next
                _errorMessage.value = null
                val total = pagination?.total
                hasMore = total?.let { next.size < it } ?: (incoming.size >= pageSize)
                _uiState.value = if (next.isEmpty()) {
                    BaseNetWorkListUiState.Empty
                } else {
                    BaseNetWorkListUiState.Success
                }
                _loadMoreState.value = if (hasMore) {
                    LoadMoreState.PullToLoad
                } else {
                    LoadMoreState.NoMore
                }
                if (append) currentPage += 1
            }
            .onFailure { error ->
                _errorMessage.value = UiMessage.network(error.message)
                if (!append && _listData.value.isEmpty()) {
                    _uiState.value = BaseNetWorkListUiState.Error
                }
                _loadMoreState.value = LoadMoreState.Error
            }
    }
}

