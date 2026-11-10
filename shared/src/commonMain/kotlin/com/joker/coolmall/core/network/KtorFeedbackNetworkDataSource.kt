package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.Feedback
import com.joker.coolmall.core.model.request.FeedbackSubmitRequest
import com.joker.coolmall.core.model.request.PageRequest
import com.joker.coolmall.core.model.response.NetworkPageData
import com.joker.coolmall.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 意见反馈 API（对齐 CoolMallKotlin FeedbackService，baseUrl 已含 app/）
 */
class KtorFeedbackNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun submitFeedback(params: FeedbackSubmitRequest): NetworkResponse<Boolean> =
        client.post("feedback/submit") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()

    suspend fun getFeedbackPage(params: PageRequest): NetworkResponse<NetworkPageData<Feedback>> =
        client.post("feedback/page") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()
}
