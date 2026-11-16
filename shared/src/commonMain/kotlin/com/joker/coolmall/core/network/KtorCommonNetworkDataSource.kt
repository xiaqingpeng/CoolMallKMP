package com.joker.coolmallkmp.core.network

import com.joker.coolmallkmp.core.config.AppConfig
import com.joker.coolmallkmp.core.model.entity.OssUpload
import com.joker.coolmallkmp.core.model.request.DictDataRequest
import com.joker.coolmallkmp.core.model.response.DictDataResponse
import com.joker.coolmallkmp.core.model.response.NetworkResponse
import com.joker.coolmallkmp.core.session.AuthSession
import com.joker.coolmallkmp.core.session.networkAuthLog
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
                "token=${AuthSession.tokenSummary()} body=empty-multipart contentType=multipart/form-data",
        )
        return runCatching {
            val response = requestUploadCredentials(
                client = client,
                url = AppConfig.baseUrl + "base/comm/upload",
                token = token,
            )
            val responseText = response.body
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
                "upload credentials response http=${response.statusCode} " +
                    "path=base/comm/upload " +
                    "contentType=${response.contentType} " +
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

data class UploadCredentialsHttpResponse(
    val statusCode: Int,
    val contentType: String,
    val body: String,
)

expect suspend fun requestUploadCredentials(
    client: HttpClient,
    url: String,
    token: String,
): UploadCredentialsHttpResponse
