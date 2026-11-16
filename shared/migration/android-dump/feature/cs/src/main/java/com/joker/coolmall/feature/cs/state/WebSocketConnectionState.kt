package com.joker.coolmallkmp.feature.cs.state

/**
 * WebSocket连接状态
 *
 * @author Joker.X
 */
sealed class WebSocketConnectionState {
    object Disconnected : WebSocketConnectionState()
    object Connecting : WebSocketConnectionState()
    object Connected : WebSocketConnectionState()
    data class Error(val message: String) : WebSocketConnectionState()
}