package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.User
import com.joker.coolmall.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

/**
 * 用户信息 API（对齐 CoolMallKotlin UserInfoService）。
 */
class KtorUserNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun getPersonInfo(): NetworkResponse<User> =
        client.get("user/info/person").body()
}
