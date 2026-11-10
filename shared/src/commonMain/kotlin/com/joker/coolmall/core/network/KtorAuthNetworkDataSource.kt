package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.Auth
import com.joker.coolmall.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * 认证相关 API（从 CoolMallKotlin AuthService 迁移为 Ktor）。
 * QQ / 微信登录接口 Android 可调用；iOS UI 层不暴露入口。
 */
class KtorAuthNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun loginByPassword(params: Map<String, String>): NetworkResponse<Auth> =
        client.post("user/login/password") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()

    suspend fun loginByPhone(params: Map<String, String>): NetworkResponse<Auth> =
        client.post("user/login/phone") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()

    suspend fun register(params: Map<String, String>): NetworkResponse<Auth> =
        client.post("user/login/register") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()

    suspend fun getSmsCode(params: Map<String, String>): NetworkResponse<String> =
        client.post("user/login/smsCode") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()

    suspend fun refreshToken(params: Map<String, String>): NetworkResponse<Auth> =
        client.post("user/login/refreshToken") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()

    suspend fun loginByQqApp(params: Map<String, String>): NetworkResponse<Auth> =
        client.post("user/login/qq") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()

    suspend fun loginByWxApp(params: Map<String, String>): NetworkResponse<Auth> =
        client.post("user/login/wxApp") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }.body()

    suspend fun getCaptcha(): NetworkResponse<Map<String, String>> =
        client.get("user/login/captcha").body()
}
