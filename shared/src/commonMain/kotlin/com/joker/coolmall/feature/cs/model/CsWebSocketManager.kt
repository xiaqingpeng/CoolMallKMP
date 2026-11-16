package com.joker.coolmallkmp.feature.cs.model

import com.joker.coolmallkmp.core.model.entity.CsMsg
import com.joker.coolmallkmp.core.network.NetworkJson
import com.joker.coolmallkmp.core.network.platformHttpClient
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.URLProtocol
import io.ktor.http.path
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Socket.IO over WebSocket（对齐 CoolMallKotlin WebSocketManager）
 * 地址：wss://mall.dusksnow.top/socket.io/?EIO=4&transport=websocket
 */
class CsWebSocketManager {
    private val _connectionState =
        MutableStateFlow<WebSocketConnectionState>(WebSocketConnectionState.Disconnected)
    val connectionState: StateFlow<WebSocketConnectionState> = _connectionState.asStateFlow()

    private var onMessageReceived: ((CsMsg) -> Unit)? = null
    private var session: DefaultClientWebSocketSession? = null
    private var sessionJob: Job? = null
    private var scopeRef: CoroutineScope? = null
    private val sendMutex = Mutex()

    private val wsClient: HttpClient by lazy {
        platformHttpClient {
            install(WebSockets)
        }
    }

    fun setOnMessageReceived(callback: (CsMsg) -> Unit) {
        onMessageReceived = callback
    }

    fun isConnected(): Boolean =
        _connectionState.value == WebSocketConnectionState.Connected

    fun connect(token: String, scope: CoroutineScope) {
        if (_connectionState.value == WebSocketConnectionState.Connecting ||
            _connectionState.value == WebSocketConnectionState.Connected
        ) {
            return
        }
        disconnect()
        scopeRef = scope
        _connectionState.value = WebSocketConnectionState.Connecting
        sessionJob = scope.launch {
            try {
                wsClient.webSocket(
                    request = {
                        url {
                            protocol = URLProtocol.WSS
                            host = "mall.dusksnow.top"
                            path("socket.io/")
                            parameters.append("EIO", "4")
                            parameters.append("transport", "websocket")
                        }
                    },
                ) {
                    session = this
                    // 认证：40/cs,{...}
                    sendText("""40/cs,{"isAdmin":false,"token":"$token"}""")
                    try {
                        while (isActive) {
                            val frame = incoming.receive()
                            if (frame is Frame.Text) {
                                handleRawMessage(frame.readText())
                            }
                        }
                    } catch (_: ClosedReceiveChannelException) {
                        // 正常关闭
                    } finally {
                        session = null
                        if (_connectionState.value != WebSocketConnectionState.Disconnected) {
                            _connectionState.value = WebSocketConnectionState.Disconnected
                        }
                    }
                }
            } catch (e: Exception) {
                _connectionState.value =
                    WebSocketConnectionState.Error(e.message ?: "连接失败")
            }
        }
    }

    suspend fun sendMessage(
        sessionId: Long,
        content: String,
        type: String = "text",
    ): Boolean {
        if (!isConnected()) return false
        // 内容里的引号做简单转义，避免破坏 JSON
        val escaped = content
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
        val payload =
            """42/cs,["send",{"sessionId":$sessionId,"content":{"type":"$type","data":"$escaped"}}]"""
        return sendText(payload)
    }

    fun disconnect() {
        val current = session
        session = null
        sessionJob?.cancel()
        sessionJob = null
        if (current != null) {
            scopeRef?.launch {
                runCatching { current.close() }
            }
        }
        scopeRef = null
        _connectionState.value = WebSocketConnectionState.Disconnected
    }

    private suspend fun sendText(text: String): Boolean = sendMutex.withLock {
        val s = session ?: return false
        return try {
            s.send(Frame.Text(text))
            true
        } catch (_: Exception) {
            false
        }
    }

    private suspend fun handleRawMessage(text: String) {
        when {
            text == "2" -> {
                // Engine.IO ping → pong
                sendText("3")
            }
            text.startsWith("40/cs,") ||
                text.contains("42/cs,[\"message\",\"连接成功\"]") -> {
                _connectionState.value = WebSocketConnectionState.Connected
            }
            text.startsWith("42[\"msg\",") -> {
                parseAndEmitMsg(text.substringAfter("42[\"msg\",").substringBeforeLast("]"))
            }
            text.startsWith("42/cs,[\"msg\",") -> {
                parseAndEmitMsg(text.substringAfter("42/cs,[\"msg\",").substringBeforeLast("]"))
            }
            text.startsWith("0{") -> {
                // handshake，忽略
            }
        }
    }

    private fun parseAndEmitMsg(jsonBody: String) {
        try {
            val msg = NetworkJson.decodeFromString(CsMsg.serializer(), jsonBody)
            onMessageReceived?.invoke(msg)
        } catch (_: Exception) {
            // 忽略解析失败
        }
    }
}
