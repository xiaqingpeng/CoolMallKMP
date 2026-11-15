package com.joker.coolmall.feature.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.CommonRepository
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface DocumentUiState {
    data object Loading : DocumentUiState
    data class Success(val html: String) : DocumentUiState
    data class Error(val message: UiMessage) : DocumentUiState
}

class DocumentViewModel(
    private val paramKey: String,
    private val commonRepository: CommonRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<DocumentUiState>(DocumentUiState.Loading)
    val uiState: StateFlow<DocumentUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = DocumentUiState.Loading
            runCatching {
                commonRepository.getParam(paramKey)
            }.onSuccess { response ->
                when {
                    response.isSucceeded && !response.data.isNullOrBlank() -> {
                        _uiState.value = if (htmlToPlainText(response.data).isBlank()) {
                            DocumentUiState.Error(UiMessage.Code(MessageCode.DocEmpty))
                        } else {
                            DocumentUiState.Success(response.data)
                        }
                    }
                    response.isSucceeded -> {
                        _uiState.value = DocumentUiState.Error(UiMessage.Code(MessageCode.DocEmpty))
                    }
                    else -> {
                        _uiState.value = DocumentUiState.Error(
                            UiMessage.loadFailed(response.message, response.code),
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.value = DocumentUiState.Error(UiMessage.network(error.message))
            }
        }
    }
}

/**
 * 简易去标签：双端不依赖 WebView，协议内容按纯文本阅读。
 */
internal fun htmlToPlainText(html: String): String {
    var text = html
        .replace(Regex("(?i)<br\\s*/?>"), "\n")
        .replace(Regex("(?i)</p>"), "\n\n")
        .replace(Regex("(?i)</div>"), "\n")
        .replace(Regex("(?i)</li>"), "\n")
        .replace(Regex("(?i)<li[^>]*>"), "· ")
        .replace(Regex("<[^>]+>"), "")
    text = text
        .replace("&nbsp;", " ")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
    return text
        .lines()
        .joinToString("\n") { it.trimEnd() }
        .replace(Regex("\n{3,}"), "\n\n")
        .trim()
}
