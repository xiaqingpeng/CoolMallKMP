package com.joker.coolmallkmp.feature.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.GoodsRepository
import com.joker.coolmallkmp.core.model.entity.CategoryTree
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.joker.coolmallkmp.core.ui.UiMessage

sealed interface CategoryUiState {
    data object Loading : CategoryUiState
    data class Success(val categories: List<CategoryTree>) : CategoryUiState
    data class Error(val message: UiMessage) : CategoryUiState
}

class CategoryViewModel(
    private val goodsRepository: GoodsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoryUiState>(CategoryUiState.Loading)
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.value = CategoryUiState.Loading
            runCatching {
                goodsRepository.getCategoryTree()
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    val data = response.data.orEmpty()
                    _uiState.value = CategoryUiState.Success(data)
                    if (_selectedIndex.value >= data.size) {
                        _selectedIndex.value = 0
                    }
                } else {
                    _uiState.value = CategoryUiState.Error(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                _uiState.value = CategoryUiState.Error(UiMessage.network(error.message))
            }
        }
    }

    fun selectCategory(index: Int) {
        _selectedIndex.value = index
    }
}
