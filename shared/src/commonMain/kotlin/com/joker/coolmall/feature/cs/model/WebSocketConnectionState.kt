package com.joker.coolmallkmp.feature.cs.model

/**
 * 客服 Socket.IO WebSocket 连接状态
 */
sealed class WebSocketConnectionState {
    data object Disconnected : WebSocketConnectionState()
    data object Connecting : WebSocketConnectionState()
    data object Connected : WebSocketConnectionState()
    data class Error(val message: String) : WebSocketConnectionState()
}
