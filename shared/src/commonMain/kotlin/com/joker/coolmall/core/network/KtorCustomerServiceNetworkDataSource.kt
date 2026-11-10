package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.CsMsg
import com.joker.coolmall.core.model.entity.CsSession
import com.joker.coolmall.core.model.request.MessagePageRequest
import com.joker.coolmall.core.model.request.ReadMessageRequest
import com.joker.coolmall.core.model.response.NetworkPageData
import com.joker.coolmall.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 客服 REST API（对齐 CoolMallKotlin CustomerServiceService）
 */
class KtorCustomerServiceNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun createSession(): NetworkResponse<CsSession> =
        client.post("cs/session/create") {
            contentType(ContentType.Application.Json)
        }.body()

    suspend fun getSessionDetail(): NetworkResponse<CsSession> =
        client.get("cs/session/detail").body()

    suspend fun readMessage(request: ReadMessageRequest): NetworkResponse<Boolean> =
        client.post("cs/msg/read") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getMessagePage(
        request: MessagePageRequest,
    ): NetworkResponse<NetworkPageData<CsMsg>> =
        client.post("cs/msg/page") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    suspend fun getUnreadCount(): NetworkResponse<Int> =
        client.get("cs/msg/unreadCount").body()
}
