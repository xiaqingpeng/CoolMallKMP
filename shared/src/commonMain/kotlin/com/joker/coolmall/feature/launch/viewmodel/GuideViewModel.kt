package com.joker.coolmallkmp.feature.launch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.local.GuidePreferences
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GuideViewModel(
    private val guidePreferences: GuidePreferences,
) : ViewModel() {

    val pageCount: Int = 4

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _navigateToMain = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToMain: SharedFlow<Unit> = _navigateToMain.asSharedFlow()

    fun updatePageIndex(index: Int) {
        _currentPage.value = index.coerceIn(0, pageCount - 1)
    }

    fun isLastPage(): Boolean = _currentPage.value >= pageCount - 1

    /** @return true 表示需要翻到下一页；false 表示已完成引导 */
    fun handleNextClick(): Boolean {
        return if (isLastPage()) {
            finishGuide()
            false
        } else {
            true
        }
    }

    fun nextPageIndex(): Int = (_currentPage.value + 1).coerceAtMost(pageCount - 1)

    fun skipGuide() {
        finishGuide()
    }

    fun finishGuide() {
        viewModelScope.launch {
            guidePreferences.setGuideShown(true)
            _navigateToMain.emit(Unit)
        }
    }
}
