package com.joker.coolmallkmp.feature.goods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.GoodsRepository
import com.joker.coolmallkmp.core.model.entity.Goods
import com.joker.coolmallkmp.core.model.entity.Category
import com.joker.coolmallkmp.core.model.request.GoodsSearchRequest
import com.joker.coolmallkmp.core.model.response.NetworkPageData
import com.joker.coolmallkmp.core.model.response.NetworkPageMeta
import com.joker.coolmallkmp.core.model.response.NetworkResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage

sealed interface GoodsCategoryUiState {
    data object Loading : GoodsCategoryUiState
    data class Success(
        val goods: List<Goods>,
        val hasMore: Boolean,
    ) : GoodsCategoryUiState

    data class Error(val message: UiMessage) : GoodsCategoryUiState
}

sealed interface GoodsCategoryFilterUiState {
    data object Loading : GoodsCategoryFilterUiState
    data class Success(val categories: List<Category>) : GoodsCategoryFilterUiState
    data class Error(val message: UiMessage) : GoodsCategoryFilterUiState
}

enum class GoodsSortType {
    COMPREHENSIVE,
    SALES,
    PRICE,
}

enum class GoodsSortState {
    NONE,
    ASC,
    DESC,
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
    private val _selectedCategoryIds = MutableStateFlow(typeId?.let(::listOf).orEmpty())
    val selectedCategoryIds: StateFlow<List<Long>> = _selectedCategoryIds.asStateFlow()
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()
    private val _filterUiState = MutableStateFlow<GoodsCategoryFilterUiState>(
        GoodsCategoryFilterUiState.Loading,
    )
    val filterUiState: StateFlow<GoodsCategoryFilterUiState> = _filterUiState.asStateFlow()
    private val _minPrice = MutableStateFlow("")
    val minPrice: StateFlow<String> = _minPrice.asStateFlow()
    private val _maxPrice = MutableStateFlow("")
    val maxPrice: StateFlow<String> = _maxPrice.asStateFlow()
    private val _sortType = MutableStateFlow(GoodsSortType.COMPREHENSIVE)
    val sortType: StateFlow<GoodsSortType> = _sortType.asStateFlow()
    private val _sortState = MutableStateFlow(GoodsSortState.NONE)
    val sortState: StateFlow<GoodsSortState> = _sortState.asStateFlow()
    private val _filtersVisible = MutableStateFlow(false)
    val filtersVisible: StateFlow<Boolean> = _filtersVisible.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        currentPage = 1
        hasMore = true
        loadedGoods.clear()
        loadPage(isRefresh = true)
    }

    fun showFilters() {
        _filtersVisible.value = true
        if (_categories.value.isEmpty()) {
            _filterUiState.value = GoodsCategoryFilterUiState.Loading
            viewModelScope.launch {
                runCatching { goodsRepository.getGoodsTypeList() }
                    .onSuccess { response ->
                        if (response.isSucceeded) {
                            val categories = response.data.orEmpty()
                            _categories.value = categories
                            _filterUiState.value = GoodsCategoryFilterUiState.Success(categories)
                        } else {
                            _filterUiState.value = GoodsCategoryFilterUiState.Error(
                                UiMessage.loadFailed(response.message, response.code),
                            )
                        }
                    }
                    .onFailure { error ->
                        _filterUiState.value = GoodsCategoryFilterUiState.Error(
                            UiMessage.network(error.message),
                        )
                    }
            }
        } else {
            _filterUiState.value = GoodsCategoryFilterUiState.Success(_categories.value)
        }
    }

    fun retryFilters() {
        _categories.value = emptyList()
        showFilters()
    }

    fun hideFilters() {
        _filtersVisible.value = false
    }

    fun applyFilters(categoryIds: List<Long>, minPrice: String, maxPrice: String) {
        _selectedCategoryIds.value = categoryIds
        _minPrice.value = minPrice.trim()
        _maxPrice.value = maxPrice.trim()
        hideFilters()
        refresh()
    }

    fun resetFilters() {
        _selectedCategoryIds.value = typeId?.let(::listOf).orEmpty()
        _minPrice.value = ""
        _maxPrice.value = ""
        hideFilters()
        refresh()
    }

    fun cycleSort(type: GoodsSortType) {
        if (type == GoodsSortType.COMPREHENSIVE) {
            _sortType.value = type
            _sortState.value = GoodsSortState.NONE
        } else if (_sortType.value != type) {
            _sortType.value = type
            _sortState.value = GoodsSortState.ASC
        } else {
            _sortState.value = when (_sortState.value) {
                GoodsSortState.NONE -> GoodsSortState.ASC
                GoodsSortState.ASC -> GoodsSortState.DESC
                GoodsSortState.DESC -> {
                    _sortType.value = GoodsSortType.COMPREHENSIVE
                    GoodsSortState.NONE
                }
            }
        }
        refresh()
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
                val request = GoodsSearchRequest(
                    page = currentPage,
                    size = pageSize,
                    typeId = _selectedCategoryIds.value.takeIf { it.isNotEmpty() },
                    minPrice = _minPrice.value.takeIf { it.isNotBlank() },
                    maxPrice = _maxPrice.value.takeIf { it.isNotBlank() },
                    order = when (_sortType.value) {
                        GoodsSortType.COMPREHENSIVE -> null
                        GoodsSortType.SALES -> "sold"
                        GoodsSortType.PRICE -> "price"
                    },
                    sort = when (_sortState.value) {
                        GoodsSortState.NONE -> null
                        GoodsSortState.ASC -> "asc"
                        GoodsSortState.DESC -> "desc"
                    },
                    featured = featured,
                    recommend = recommend,
                    keyWord = keyword?.takeIf { it.isNotBlank() },
                )
                val response = goodsRepository.getGoodsPage(request)
                if (
                    response.isSucceeded &&
                    response.data?.list.isNullOrEmpty() &&
                    !keyword.isNullOrBlank() &&
                    typeId == null &&
                    !featured &&
                    !recommend
                ) {
                    val splitMatches = keyword
                        .trim()
                        .split(Regex("\\s+"))
                        .map { it.trim() }
                        .flatMap(::searchTokens)
                        .filter { !it.equals(keyword.trim(), ignoreCase = true) }
                        .distinct()
                        .flatMap { token ->
                            goodsRepository.getGoodsPage(
                                request.copy(
                                    keyWord = token,
                                    size = pageSize,
                                ),
                            ).data?.list.orEmpty()
                        }
                        .distinctBy { it.id }
                    if (splitMatches.isNotEmpty()) {
                        NetworkResponse(
                            data = NetworkPageData(
                                list = splitMatches,
                                pagination = NetworkPageMeta(
                                    total = splitMatches.size,
                                    size = splitMatches.size,
                                    page = 1,
                                ),
                            ),
                            code = response.code,
                            message = response.message,
                        )
                    } else {
                        val matchedTypeIds = goodsRepository.getGoodsTypeList()
                            .data
                            .orEmpty()
                            .let { categories ->
                                val matchedRoots = categories
                                    .filter { category ->
                                        category.name.contains(keyword, ignoreCase = true) ||
                                            keyword.contains(category.name, ignoreCase = true)
                                    }
                                    .map { it.id }
                                    .toSet()
                                if (matchedRoots.isEmpty()) {
                                    emptyList()
                                } else {
                                    categories
                                        .filter { category ->
                                            category.id in matchedRoots ||
                                                category.parentId?.toLong() in matchedRoots
                                        }
                                        .map { it.id }
                                }
                            }
                        if (matchedTypeIds.isNotEmpty()) {
                            goodsRepository.getGoodsPage(
                                request.copy(
                                    typeId = matchedTypeIds,
                                    keyWord = null,
                                ),
                            )
                        } else {
                            response
                        }
                    }
                } else {
                    response
                }
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

    private fun searchTokens(token: String): List<String> {
        if (token.length < 2) return emptyList()
        if (token.none { it in '\u4e00'..'\u9fff' }) return listOf(token)

        val tokens = linkedSetOf(token)
        for (length in 2..minOf(4, token.length)) {
            for (start in 0..token.length - length) {
                tokens += token.substring(start, start + length)
            }
        }
        return tokens.toList()
    }
}
