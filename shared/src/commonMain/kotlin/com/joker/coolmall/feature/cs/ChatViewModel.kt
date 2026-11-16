package com.joker.coolmallkmp.feature.cs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joker.coolmallkmp.core.data.repository.CustomerServiceRepository
import com.joker.coolmallkmp.core.common.base.state.ReversePagedListState
import com.joker.coolmallkmp.core.data.repository.FileUploadRepository
import com.joker.coolmallkmp.core.model.entity.CsMsg
import com.joker.coolmallkmp.core.platform.PickedImage
import com.joker.coolmallkmp.core.session.AuthSession
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import com.joker.coolmallkmp.core.ui.MessageCode
import com.joker.coolmallkmp.core.ui.UiMessage

sealed class ChatUiState {
    data object Loading : ChatUiState()
    data class Error(val message: UiMessage) : ChatUiState()
    data object Success : ChatUiState()
}

class ChatViewModel(
    private val customerServiceRepository: CustomerServiceRepository,
    private val fileUploadRepository: FileUploadRepository,
) : ViewModel() {

    private val webSocketManager = CsWebSocketManager()

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _historyState = MutableStateFlow(ReversePagedListState<CsMsg>())
    val messages: StateFlow<List<CsMsg>> = _historyState
        .map { it.items }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    val isLoadingHistory: StateFlow<Boolean> = _historyState
        .map { it.loading }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val hasMore: StateFlow<Boolean> = _historyState
        .map { it.hasMore }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    private val _isSendingImage = MutableStateFlow(false)
    val isSendingImage: StateFlow<Boolean> = _isSendingImage.asStateFlow()

    private val _connectionState =
        MutableStateFlow<WebSocketConnectionState>(WebSocketConnectionState.Disconnected)
    val connectionState: StateFlow<WebSocketConnectionState> = _connectionState.asStateFlow()

    private val _messageEvent = MutableSharedFlow<UiMessage>(extraBufferCapacity = 1)
    val messageEvent: SharedFlow<UiMessage> = _messageEvent.asSharedFlow()

    private var sessionId: Long = 0
    private val pageSize = 10

    init {
        webSocketManager.setOnMessageReceived { msg -> addIncomingMessage(msg) }
        viewModelScope.launch {
            webSocketManager.connectionState.collect { _connectionState.value = it }
        }
        createSession()
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun retry() {
        createSession()
    }

    fun loadMore() {
        if (_historyState.value.loading || !_historyState.value.hasMore || sessionId <= 0) return
        _historyState.value = _historyState.value.copy(
            page = _historyState.value.page + 1,
        )
        loadHistory(isInitial = false)
    }

    fun sendMessage() {
        val content = _inputText.value.trim()
        if (content.isEmpty()) return
        if (!ensureCanSend()) return
        viewModelScope.launch {
            val ok = webSocketManager.sendMessage(sessionId, content, "text")
            if (ok) {
                _inputText.value = ""
            } else {
                _messageEvent.emit(UiMessage.Code(MessageCode.CsSendFailed))
                connectSocket()
            }
        }
    }

    /**
     * 选图后上传 OSS，再以 type=image 经 WebSocket 发送图片 URL。
     */
    fun onImagesPicked(images: List<PickedImage>) {
        val image = images.firstOrNull() ?: return
        if (_isSendingImage.value) return
        if (!ensureCanSend()) return
        viewModelScope.launch {
            _isSendingImage.value = true
            runCatching { fileUploadRepository.uploadImages(listOf(image)) }
                .onSuccess { resp ->
                    val url = resp.data?.firstOrNull()
                    if (!resp.isSucceeded || url.isNullOrBlank()) {
                        _messageEvent.emit(UiMessage.rawOrCode(resp.message, MessageCode.CsUploadFailed))
                        _isSendingImage.value = false
                        return@onSuccess
                    }
                    val ok = webSocketManager.sendMessage(sessionId, url, "image")
                    if (!ok) {
                        _messageEvent.emit(UiMessage.Code(MessageCode.CsSendFailed))
                        connectSocket()
                    }
                    _isSendingImage.value = false
                }
                .onFailure { e ->
                    _messageEvent.emit(UiMessage.rawOrCode(e.message, MessageCode.CsUploadFailed))
                    _isSendingImage.value = false
                }
        }
    }

    private fun ensureCanSend(): Boolean {
        if (sessionId <= 0) {
            viewModelScope.launch { _messageEvent.emit(UiMessage.Code(MessageCode.CsSessionNotReady)) }
            return false
        }
        if (!webSocketManager.isConnected()) {
            connectSocket()
            viewModelScope.launch { _messageEvent.emit(UiMessage.Code(MessageCode.CsConnectingRetry)) }
            return false
        }
        return true
    }

    private fun createSession() {
        _uiState.value = ChatUiState.Loading
        viewModelScope.launch {
            runCatching { customerServiceRepository.createSession() }
                .onSuccess { resp ->
                    if (!resp.isSucceeded || resp.data == null) {
                        _uiState.value = ChatUiState.Error(UiMessage.rawOrCode(resp.message, MessageCode.CsCreateSessionFailed))
                        return@onSuccess
                    }
                    sessionId = resp.data.id
                    connectSocket()
                    _historyState.value = ReversePagedListState()
                    _historyState.value = ReversePagedListState()
                    loadHistory(isInitial = true)
                }
                .onFailure { e ->
                    _uiState.value = ChatUiState.Error(UiMessage.network(e.message))
                }
        }
    }

    private fun loadHistory(isInitial: Boolean) {
        if (_historyState.value.loading) return
        val sid = sessionId
        if (sid <= 0) return
        _historyState.value = _historyState.value.copy(loading = true)
        val currentPage = _historyState.value.page
        viewModelScope.launch {
            runCatching {
                customerServiceRepository.getMessagePage(
                    sessionId = sid,
                    page = currentPage,
                    size = pageSize,
                )
            }.onSuccess { resp ->
                _historyState.value = _historyState.value.copy(loading = false)
                if (!resp.isSucceeded) {
                    if (isInitial) {
                        _uiState.value = ChatUiState.Error(UiMessage.rawOrCode(resp.message, MessageCode.CsLoadMessagesFailed))
                    } else {
                        _historyState.value = _historyState.value.copy(
                            page = (currentPage - 1).coerceAtLeast(1),
                        )
                    }
                    return@onSuccess
                }
                val page = resp.data
                val list = page?.list.orEmpty()
                val pagination = page?.pagination
                val hasMore = if (pagination?.total != null) {
                    val total = pagination.total!!
                    val size = pagination.size ?: pageSize
                    val pageNum = pagination.page ?: currentPage
                    size * pageNum < total
                } else {
                    list.size >= pageSize
                }
                _historyState.value = if (isInitial || currentPage == 1) {
                    _historyState.value.copy(
                        page = currentPage,
                        items = list,
                        hasMore = hasMore,
                        loading = false,
                    )
                } else {
                    val existing = _historyState.value.items.map { it.id }.toSet()
                    val unique = list.filter { it.id !in existing }
                    _historyState.value.copy(
                        page = currentPage,
                        items = _historyState.value.items + unique,
                        hasMore = hasMore,
                        loading = false,
                    )
                }
                if (isInitial || currentPage == 1) {
                    _uiState.value = ChatUiState.Success
                    markUnreadRead()
                }
            }.onFailure { e ->
                _historyState.value = _historyState.value.copy(loading = false)
                if (isInitial) {
                    _uiState.value = ChatUiState.Error(UiMessage.rawOrCode(e.message, MessageCode.CsLoadMessagesFailed))
                } else {
                    _historyState.value = _historyState.value.copy(
                        page = (_historyState.value.page - 1).coerceAtLeast(1),
                    )
                }
            }
        }
    }

    private fun connectSocket() {
        val token = AuthSession.token
        if (token.isBlank()) return
        webSocketManager.connect(token, viewModelScope)
    }

    private fun addIncomingMessage(message: CsMsg) {
        val current = _historyState.value.items
        if (current.any { it.id == message.id }) return
        _historyState.value = _historyState.value.copy(items = listOf(message) + current)
        if (message.type == 1 && message.status == 0) {
            viewModelScope.launch {
                runCatching {
                    customerServiceRepository.readMessage(listOf(message.id))
                }
            }
        }
    }

    private fun markUnreadRead() {
        val ids = _historyState.value.items.filter { it.status == 0 }.map { it.id }
        if (ids.isEmpty()) return
        viewModelScope.launch {
            runCatching { customerServiceRepository.readMessage(ids) }
        }
    }

    override fun onCleared() {
        webSocketManager.disconnect()
        super.onCleared()
    }
}
