package com.joker.coolmall.feature.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmall.core.data.repository.CommonRepository
import com.joker.coolmall.core.data.repository.FeedbackRepository
import com.joker.coolmall.core.data.repository.FileUploadRepository
import com.joker.coolmall.core.model.entity.DictItem
import com.joker.coolmall.core.model.request.FeedbackSubmitRequest
import com.joker.coolmall.core.platform.PickedImage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.joker.coolmall.core.ui.MessageCode
import com.joker.coolmall.core.ui.UiMessage

sealed interface FeedbackTypesUiState {
    data object Loading : FeedbackTypesUiState
    data class Success(val types: List<DictItem>) : FeedbackTypesUiState
    data class Error(val message: UiMessage) : FeedbackTypesUiState
}

class FeedbackSubmitViewModel(
    private val feedbackRepository: FeedbackRepository,
    private val commonRepository: CommonRepository,
    private val fileUploadRepository: FileUploadRepository,
) : ViewModel() {

    private val maxImages = 9

    private val _typesState =
        MutableStateFlow<FeedbackTypesUiState>(FeedbackTypesUiState.Loading)
    val typesState: StateFlow<FeedbackTypesUiState> = _typesState.asStateFlow()

    private val _selectedType = MutableStateFlow<DictItem?>(null)
    val selectedType: StateFlow<DictItem?> = _selectedType.asStateFlow()

    private val _contact = MutableStateFlow("")
    val contact: StateFlow<String> = _contact.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    private val _pendingImages = MutableStateFlow<List<PickedImage>>(emptyList())
    val pendingImages: StateFlow<List<PickedImage>> = _pendingImages.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    private val _submitted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val submitted: SharedFlow<Unit> = _submitted.asSharedFlow()

    init {
        loadTypes()
    }

    fun loadTypes() {
        viewModelScope.launch {
            _typesState.value = FeedbackTypesUiState.Loading
            runCatching {
                commonRepository.getFeedbackTypes()
            }.onSuccess { response ->
                if (response.isSucceeded) {
                    val types = response.data.orEmpty()
                    _typesState.value = FeedbackTypesUiState.Success(types)
                    if (_selectedType.value == null) {
                        _selectedType.value = types.firstOrNull()
                    }
                } else {
                    // 字典失败时提供兜底类型，仍可提交
                    val fallback = listOf(
                        DictItem(name = "suggestion", value = 1),
                        DictItem(name = "experience", value = 2),
                        DictItem(name = "other", value = 0),
                    )
                    _typesState.value = FeedbackTypesUiState.Success(fallback)
                    _selectedType.value = fallback.first()
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.FeedbackTypeLoadFailed))
                }
            }.onFailure {
                val fallback = listOf(
                    DictItem(name = "suggestion", value = 1),
                    DictItem(name = "experience", value = 2),
                    DictItem(name = "other", value = 0),
                )
                _typesState.value = FeedbackTypesUiState.Success(fallback)
                _selectedType.value = fallback.first()
            }
        }
    }

    fun selectType(item: DictItem) {
        _selectedType.value = item
    }

    fun updateContact(value: String) {
        _contact.value = value
    }

    fun updateContent(value: String) {
        _content.value = value
    }

    fun remainingImageSlots(): Int =
        (maxImages - _pendingImages.value.size).coerceAtLeast(0)

    fun onImagesPicked(picked: List<PickedImage>) {
        if (picked.isEmpty()) return
        val remaining = remainingImageSlots()
        if (remaining <= 0) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.FeedbackMaxImages, maxImages.toString())) }
            return
        }
        _pendingImages.update { current ->
            (current + picked).distinctBy { it.uri }.take(maxImages)
        }
    }

    fun removePendingImage(uri: String) {
        _pendingImages.update { list -> list.filterNot { it.uri == uri } }
    }

    fun submit() {
        if (_isSubmitting.value) return
        val type = _selectedType.value
        val text = _content.value.trim()
        if (type == null) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.FeedbackNeedType)) }
            return
        }
        if (text.isEmpty()) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.FeedbackNeedContent)) }
            return
        }
        viewModelScope.launch {
            _isSubmitting.value = true
            val pending = _pendingImages.value
            val uploadedUrls = if (pending.isNotEmpty()) {
                val uploadResult = fileUploadRepository.uploadImages(pending)
                if (!uploadResult.isSucceeded || uploadResult.data == null) {
                    _isSubmitting.value = false
                    _message.emit(UiMessage.rawOrCode(uploadResult.message, MessageCode.OrderCommentPicUploadFailed))
                    return@launch
                }
                uploadResult.data
            } else {
                emptyList()
            }
            val request = FeedbackSubmitRequest(
                contact = _contact.value.trim().ifBlank { null },
                type = type.value ?: 0,
                content = text,
                images = uploadedUrls.ifEmpty { null },
            )
            runCatching {
                feedbackRepository.submitFeedback(request)
            }.onSuccess { response ->
                _isSubmitting.value = false
                if (response.isSucceeded) {
                    _message.emit(UiMessage.Code(MessageCode.FeedbackSubmitSuccess))
                    _submitted.emit(Unit)
                } else {
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.FeedbackSubmitFailed, response.code.toString()))
                }
            }.onFailure { error ->
                _isSubmitting.value = false
                _message.emit(UiMessage.network(error.message))
            }
        }
    }
}
