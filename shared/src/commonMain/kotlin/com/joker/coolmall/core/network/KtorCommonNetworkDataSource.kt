package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.OssUpload
import com.joker.coolmall.core.model.request.DictDataRequest
import com.joker.coolmall.core.model.response.DictDataResponse
import com.joker.coolmall.core.model.response.NetworkResponse
import com.joker.coolmall.core.session.AuthSession
import com.joker.coolmall.core.session.networkAuthLog
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.decodeFromString

/**
 * 通用基础 API（对齐 CoolMallKotlin CommonService，仅迁当前需要部分）
 */
class KtorCommonNetworkDataSource(
    private val client: HttpClient,
) {
    /** 获取字典数据 */
    suspend fun getDictData(request: DictDataRequest): NetworkResponse<DictDataResponse> =
        client.post("dict/info/data") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()

    /** 获取系统参数配置 */
    suspend fun getParam(key: String): NetworkResponse<String> =
        client.get("base/comm/param") {
            parameter("key", key)
        }.body()

    /** 获取腾讯云上传凭证 */
    suspend fun uploadFile(): NetworkResponse<OssUpload> {
        val token = AuthSession.token.trim()
        networkAuthLog(
            "upload credentials request method=POST path=base/comm/upload " +
                "token=${AuthSession.tokenSummary()} body=none contentType=<none>",
        )
        return runCatching {
            val response = client.post("base/comm/upload") {
                // CoolMallKotlin's Retrofit @POST has no request body or JSON content type.
                headers.remove(HttpHeaders.Authorization)
                headers[HttpHeaders.Authorization] = token
                headers.remove(HttpHeaders.ContentType)
            }
            val responseText = response.bodyAsText()
            val safeResponseText = responseText
                .replace(
                    Regex("\"tmpSecretKey\"\\s*:\\s*\"[^\"]*\""),
                    "\"tmpSecretKey\":\"<redacted>\"",
                )
                .replace(
                    Regex("\"tmpSecretId\"\\s*:\\s*\"[^\"]*\""),
                    "\"tmpSecretId\":\"<redacted>\"",
                )
                .replace(
                    Regex("\"sessionToken\"\\s*:\\s*\"[^\"]*\""),
                    "\"sessionToken\":\"<redacted>\"",
                )
            networkAuthLog(
                "upload credentials response http=${response.status.value} " +
                    "path=base/comm/upload " +
                    "contentType=${response.headers[HttpHeaders.ContentType].orEmpty()} " +
                    "body=${safeResponseText.take(1000)}",
            )
            NetworkJson.decodeFromString<NetworkResponse<OssUpload>>(responseText)
        }.onFailure {
            networkAuthLog(
                "upload credentials failure type=${it::class.simpleName} message=${it.message.orEmpty()}",
            )
        }.getOrElse {
            NetworkResponse(data = null, code = 500, message = it.message ?: "获取上传凭证失败")
        }
    }
}
