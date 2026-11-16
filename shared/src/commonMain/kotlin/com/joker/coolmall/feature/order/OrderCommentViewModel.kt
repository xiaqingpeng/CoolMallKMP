package com.joker.coolmallkmp.feature.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.FileUploadRepository
import com.joker.coolmallkmp.core.data.repository.GoodsRepository
import com.joker.coolmallkmp.core.data.repository.OrderRepository
import com.joker.coolmallkmp.core.model.entity.Comment
import com.joker.coolmallkmp.core.model.entity.OrderGoods
import com.joker.coolmallkmp.core.model.request.GoodsCommentSubmitRequest
import com.joker.coolmallkmp.core.platform.PickedImage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage

sealed interface OrderCommentUiState {
    data object Loading : OrderCommentUiState
    data class Success(
        val goodsList: List<OrderGoods>,
        val selectedGoodsId: Long,
    ) : OrderCommentUiState

    data class Error(val message: UiMessage) : OrderCommentUiState
}

/**
 * 订单评价：加载订单商品 → 选商品 → 星级 + 文案 + 配图提交。
 * 配图：Compose 平台选图（Photo Picker / PHPicker）上传，也可手动添加已上传 URL。
 */
class OrderCommentViewModel(
    private val orderId: Long,
    private val initialGoodsId: Long,
    private val orderRepository: OrderRepository,
    private val goodsRepository: GoodsRepository,
    private val fileUploadRepository: FileUploadRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderCommentUiState>(OrderCommentUiState.Loading)
    val uiState: StateFlow<OrderCommentUiState> = _uiState.asStateFlow()

    private val _rating = MutableStateFlow(5)
    val rating: StateFlow<Int> = _rating.asStateFlow()

    private val _content = MutableStateFlow("")
    val content: StateFlow<String> = _content.asStateFlow()

    /** 已确认的配图 URL（手动添加或上传成功后的结果）。 */
    private val _picUrls = MutableStateFlow<List<String>>(emptyList())
    val picUrls: StateFlow<List<String>> = _picUrls.asStateFlow()

    /** 待上传的本地图。 */
    private val _pendingImages = MutableStateFlow<List<PickedImage>>(emptyList())
    val pendingImages: StateFlow<List<PickedImage>> = _pendingImages.asStateFlow()

    private val _manualUrlDraft = MutableStateFlow("")
    val manualUrlDraft: StateFlow<String> = _manualUrlDraft.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _message = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val message: SharedFlow<UiMessage> = _message.asSharedFlow()

    private val _submitted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val submitted: SharedFlow<Unit> = _submitted.asSharedFlow()

    private val maxPics = 9

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = OrderCommentUiState.Loading
            runCatching {
                orderRepository.getOrderInfo(orderId)
            }.onSuccess { response ->
                if (response.isSucceeded && response.data != null) {
                    val goods = response.data.goodsList.orEmpty()
                    if (goods.isEmpty()) {
                        _uiState.value = OrderCommentUiState.Error(UiMessage.Code(MessageCode.OrderCommentNoGoods))
                        return@onSuccess
                    }
                    val selected = when {
                        initialGoodsId > 0 && goods.any { it.goodsId == initialGoodsId } ->
                            initialGoodsId
                        goods.size == 1 -> goods.first().goodsId
                        else -> goods.first().goodsId
                    }
                    _uiState.value = OrderCommentUiState.Success(
                        goodsList = goods,
                        selectedGoodsId = selected,
                    )
                } else {
                    _uiState.value = OrderCommentUiState.Error(UiMessage.loadFailed(response.message, response.code))
                }
            }.onFailure { error ->
                _uiState.value = OrderCommentUiState.Error(UiMessage.network(error.message))
            }
        }
    }

    fun selectGoods(goodsId: Long) {
        val current = _uiState.value as? OrderCommentUiState.Success ?: return
        _uiState.value = current.copy(selectedGoodsId = goodsId)
    }

    fun updateRating(value: Int) {
        _rating.value = value.coerceIn(1, 5)
    }

    fun updateContent(value: String) {
        _content.value = value
    }

    fun updateManualUrlDraft(value: String) {
        _manualUrlDraft.value = value
    }

    fun addManualPicUrl() {
        val url = _manualUrlDraft.value.trim()
        if (url.isEmpty()) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.OrderCommentPicUrlRequired)) }
            return
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.OrderCommentPicUrlInvalid)) }
            return
        }
        val total = _picUrls.value.size + _pendingImages.value.size
        if (total >= maxPics) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.OrderCommentPicMax, maxPics.toString())) }
            return
        }
        if (url in _picUrls.value) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.OrderCommentPicDup)) }
            return
        }
        _picUrls.update { it + url }
        _manualUrlDraft.value = ""
    }

    fun removePicUrl(url: String) {
        _picUrls.update { list -> list.filterNot { it == url } }
    }

    fun removePendingImage(uri: String) {
        _pendingImages.update { list -> list.filterNot { it.uri == uri } }
    }

    fun remainingPicSlots(): Int =
        (maxPics - _picUrls.value.size - _pendingImages.value.size).coerceAtLeast(0)

    fun onImagesPicked(picked: List<PickedImage>) {
        if (picked.isEmpty()) return
        val remaining = remainingPicSlots()
        if (remaining <= 0) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.OrderCommentPicMax, maxPics.toString())) }
            return
        }
        _pendingImages.update { current ->
            (current + picked).distinctBy { it.uri }.take(maxPics - _picUrls.value.size)
        }
    }

    fun submit() {
        if (_isSubmitting.value) return
        val state = _uiState.value as? OrderCommentUiState.Success
        if (state == null) return
        if (_rating.value <= 0) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.OrderCommentNeedRating)) }
            return
        }
        val text = _content.value.trim()
        if (text.isEmpty()) {
            viewModelScope.launch { _message.emit(UiMessage.Code(MessageCode.OrderCommentNeedContent)) }
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
            val allPics = (_picUrls.value + uploadedUrls).distinct().ifEmpty { null }
            val request = GoodsCommentSubmitRequest(
                orderId = orderId,
                data = Comment(
                    orderId = orderId,
                    goodsId = state.selectedGoodsId,
                    content = text,
                    starCount = _rating.value,
                    pics = allPics,
                ),
            )
            runCatching {
                goodsRepository.submitGoodsComment(request)
            }.onSuccess { response ->
                _isSubmitting.value = false
                if (response.isSucceeded) {
                    if (uploadedUrls.isNotEmpty()) {
                        _picUrls.update { it + uploadedUrls }
                        _pendingImages.value = emptyList()
                    }
                    _message.emit(UiMessage.Code(MessageCode.OrderCommentSuccess))
                    _submitted.emit(Unit)
                } else {
                    _message.emit(UiMessage.rawOrCode(response.message, MessageCode.OrderCommentSubmitFailed, response.code.toString()))
                }
            }.onFailure { error ->
                _isSubmitting.value = false
                _message.emit(UiMessage.network(error.message))
            }
        }
    }
}
