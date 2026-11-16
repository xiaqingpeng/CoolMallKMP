package com.joker.coolmallkmp.feature.goods.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.SearchHistoryRepository
import com.joker.coolmallkmp.core.data.repository.GoodsRepository
import com.joker.coolmallkmp.core.model.entity.GoodsSearchKeyword
import com.joker.coolmallkmp.core.ui.UiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface GoodsSearchUiState {
    data object Loading : GoodsSearchUiState
    data class Success(val keywords: List<GoodsSearchKeyword>) : GoodsSearchUiState
    data class Error(val message: UiMessage) : GoodsSearchUiState
}

class GoodsSearchViewModel(
    private val goodsRepository: GoodsRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GoodsSearchUiState>(GoodsSearchUiState.Loading)
    val uiState: StateFlow<GoodsSearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val history: StateFlow<List<String>> = searchHistoryRepository.history
        .map { values -> values.map { it.keyword } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    init {
        loadKeywords()
    }

    fun updateQuery(value: String) {
        _query.value = value
    }

    fun loadKeywords() {
        viewModelScope.launch {
            _uiState.value = GoodsSearchUiState.Loading
            runCatching {
                goodsRepository.getSearchKeywordList()
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    _uiState.value = GoodsSearchUiState.Success(response.data.orEmpty())
                } else {
                    _uiState.value = GoodsSearchUiState.Error(
                        UiMessage.loadFailed(response.message, response.code),
                    )
                }
            }.onFailure { error ->
                _uiState.value = GoodsSearchUiState.Error(UiMessage.network(error.message))
            }
        }
    }

    /** 记录历史；返回规范化关键词，空串返回 null。导航不依赖本地存储写入完成。 */
    fun commitSearch(raw: String): String? {
        val keyword = raw.trim()
        if (keyword.isEmpty()) return null
        viewModelScope.launch {
            searchHistoryRepository.add(keyword)
        }
        return keyword
    }

    fun clearHistory() {
        viewModelScope.launch {
            searchHistoryRepository.clear()
        }
    }
}
