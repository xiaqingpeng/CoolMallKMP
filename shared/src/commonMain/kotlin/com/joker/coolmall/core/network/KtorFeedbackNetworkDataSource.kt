package com.joker.coolmallkmp.core.network

import com.joker.coolmallkmp.core.model.entity.Feedback
import com.joker.coolmallkmp.core.model.request.FeedbackSubmitRequest
import com.joker.coolmallkmp.core.model.request.PageRequest
import com.joker.coolmallkmp.core.model.response.NetworkPageData
import com.joker.coolmallkmp.core.model.response.NetworkResponse
import com.joker.coolmallkmp.core.session.AuthSession
import com.joker.coolmallkmp.core.session.networkAuthLog
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 意见反馈 API。
 *
 * CoolMallKotlin 的 Retrofit baseUrl 已经包含 /app/，而 FeedbackService
 * 的注解还额外包含 app/，因此这里必须保留同样的两层路径。
 */
class KtorFeedbackNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun submitFeedback(params: FeedbackSubmitRequest): NetworkResponse<Boolean> {
        networkAuthLog(
            "feedback submit method=POST path=app/feedback/submit token=${AuthSession.tokenSummary()} " +
                "type=${params.type} contentLength=${params.content.length} " +
                "images=${params.images?.size ?: 0}",
        )
        return runCatching {
            client.post("app/feedback/submit") {
            contentType(ContentType.Application.Json)
            headers.remove(HttpHeaders.Authorization)
            headers[HttpHeaders.Authorization] = AuthSession.token.trim()
            setBody(params)
            }.body<NetworkResponse<Boolean>>().also {
                networkAuthLog(
                    "feedback submit response code=${it.code} message=${it.message.orEmpty()} " +
                        "dataPresent=${it.data != null}",
                )
            }
        }.onFailure {
            networkAuthLog(
                "feedback submit failure type=${it::class.simpleName} message=${it.message}",
            )
        }.getOrThrow()
    }

    suspend fun getFeedbackPage(params: PageRequest): NetworkResponse<NetworkPageData<Feedback>> {
        networkAuthLog(
            "feedback page method=POST path=app/feedback/page token=${AuthSession.tokenSummary()} " +
                "page=${params.page} size=${params.size} sort=${params.sort}",
        )
        return runCatching {
            client.post("app/feedback/page") {
            contentType(ContentType.Application.Json)
            headers.remove(HttpHeaders.Authorization)
            headers[HttpHeaders.Authorization] = AuthSession.token.trim()
            setBody(params)
            }.body<NetworkResponse<NetworkPageData<Feedback>>>().also {
                networkAuthLog(
                    "feedback page response code=${it.code} message=${it.message.orEmpty()} " +
                        "dataPresent=${it.data != null} listSize=${it.data?.list?.size ?: 0}",
                )
            }
        }.onFailure {
            networkAuthLog(
                "feedback page failure type=${it::class.simpleName} message=${it.message}",
            )
        }.getOrThrow()
    }
}
