package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.OssUpload
import com.joker.coolmall.core.model.request.DictDataRequest
import com.joker.coolmall.core.model.response.DictDataResponse
import com.joker.coolmall.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.parameter
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

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
    suspend fun uploadFile(): NetworkResponse<OssUpload> =
        client.post("base/comm/upload") {
            contentType(ContentType.Application.Json)
            setBody(emptyMap<String, String>())
        }.body()
}
