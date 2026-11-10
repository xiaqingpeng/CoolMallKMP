package com.joker.coolmall.feature.goods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.GoodsRepository
import com.joker.coolmall.core.model.entity.Goods
import com.joker.coolmall.core.model.request.GoodsSearchRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage

sealed interface GoodsCategoryUiState {
    data object Loading : GoodsCategoryUiState
    data class Success(
        val goods: List<Goods>,
        val hasMore: Boolean,
    ) : GoodsCategoryUiState

    data class Error(val message: UiMessage) : GoodsCategoryUiState
}

class GoodsCategoryViewModel(
    private val typeId: Long?,
    private val title: String,
    private val featured: Boolean,
    private val recommend: Boolean,
    private val keyword: String?,
    private val goodsRepository: GoodsRepository,
) : ViewModel() {

    /** 路由传入的原始标题；为空时由 UI 用 composeResources 解析默认标题。 */
    val routeTitle: String = title
    val listFeatured: Boolean = featured
    val listRecommend: Boolean = recommend
    val listKeyword: String? = keyword

    private val _uiState = MutableStateFlow<GoodsCategoryUiState>(GoodsCategoryUiState.Loading)
    val uiState: StateFlow<GoodsCategoryUiState> = _uiState.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20
    private val loadedGoods = mutableListOf<Goods>()
    private var hasMore = true

    init {
        refresh()
    }

    fun refresh() {
        currentPage = 1
        hasMore = true
        loadedGoods.clear()
        loadPage(isRefresh = true)
    }

    fun loadMore() {
        if (!hasMore || _isLoadingMore.value || _uiState.value is GoodsCategoryUiState.Loading) {
            return
        }
        currentPage += 1
        loadPage(isRefresh = false)
    }

    private fun loadPage(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.value = GoodsCategoryUiState.Loading
            } else {
                _isLoadingMore.value = true
            }

            runCatching {
                goodsRepository.getGoodsPage(
                    GoodsSearchRequest(
                        page = currentPage,
                        size = pageSize,
                        typeId = typeId?.let { listOf(it) },
                        featured = featured,
                        recommend = recommend,
                        keyWord = keyword?.takeIf { it.isNotBlank() },
                    )
                )
            }.onSuccess { response ->
                _isLoadingMore.value = false
                if (!response.isSucceeded) {
                    if (isRefresh) {
                        _uiState.value = GoodsCategoryUiState.Error(UiMessage.loadFailed(response.message, response.code))
                    } else {
                        currentPage = (currentPage - 1).coerceAtLeast(1)
                    }
                    return@onSuccess
                }

                val pageData = response.data
                val list = pageData?.list.orEmpty()
                if (isRefresh) {
                    loadedGoods.clear()
                }
                loadedGoods.addAll(list)

                val total = pageData?.pagination?.total
                hasMore = if (total != null) {
                    loadedGoods.size < total
                } else {
                    list.size >= pageSize
                }

                _uiState.value = GoodsCategoryUiState.Success(
                    goods = loadedGoods.toList(),
                    hasMore = hasMore,
                )
            }.onFailure { error ->
                _isLoadingMore.value = false
                if (isRefresh) {
                    _uiState.value = GoodsCategoryUiState.Error(UiMessage.network(error.message))
                } else {
                    currentPage = (currentPage - 1).coerceAtLeast(1)
                }
            }
        }
    }
}
