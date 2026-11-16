package com.joker.coolmallkmp.core.network

import com.joker.coolmallkmp.core.model.entity.User
import com.joker.coolmallkmp.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 用户信息 API（对齐 CoolMallKotlin UserInfoService）。
 */
class KtorUserNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun getPersonInfo(): NetworkResponse<User> =
        client.get("user/info/person").body()

    suspend fun updatePassword(params: Map<String, String>): NetworkResponse<Unit> =
        client.post("user/info/updatePassword") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()

    suspend fun logoff(params: Map<String, String> = emptyMap()): NetworkResponse<Unit> =
        client.post("user/info/logoff") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()
}
