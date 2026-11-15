package com.joker.coolmall.core.network

import com.joker.coolmall.core.model.entity.Auth
import com.joker.coolmall.core.model.entity.Captcha
import com.joker.coolmall.core.model.response.NetworkResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import com.joker.coolmall.core.session.networkAuthLog

/**
 * 认证相关 API（从 CoolMallKotlin AuthService 迁移为 Ktor）。
 * QQ / 微信登录接口 Android 可调用；iOS UI 层不暴露入口。
 */
class KtorAuthNetworkDataSource(
    private val client: HttpClient,
) {
    suspend fun loginByPassword(params: Map<String, String>): NetworkResponse<Auth> =
        logged("user/login/password", params) {
            client.post("user/login/password") {
                contentType(ContentType.Application.Json)
                setBody(params)
            }.body()
        }

    suspend fun loginByPhone(params: Map<String, String>): NetworkResponse<Auth> =
        logged("user/login/phone", params) {
            client.post("user/login/phone") {
                contentType(ContentType.Application.Json)
                setBody(params)
            }.body()
        }

    suspend fun register(params: Map<String, String>): NetworkResponse<Auth> =
        logged("user/login/register", params) {
            client.post("user/login/register") {
                contentType(ContentType.Application.Json)
                setBody(params)
            }.body()
        }

    suspend fun getSmsCode(params: Map<String, String>): NetworkResponse<String> =
        logged("user/login/smsCode", params) {
            client.post("user/login/smsCode") {
                contentType(ContentType.Application.Json)
                setBody(params)
            }.body()
        }

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

    suspend fun getCaptcha(): NetworkResponse<Captcha> =
        client.get("user/login/captcha").body()
}

private suspend fun <T> logged(
    path: String,
    params: Map<String, String>,
    block: suspend () -> NetworkResponse<T>,
): NetworkResponse<T> {
    networkAuthLog(
        "auth request method=POST path=$path bodyKeys=${params.keys.sorted()} " +
            "token=${com.joker.coolmall.core.session.AuthSession.tokenSummary()}",
    )
    return try {
        block().also { response ->
            networkAuthLog(
                "auth response path=$path code=${response.code} " +
                    "message=${response.message.orEmpty()} dataPresent=${response.data != null}",
            )
        }
    } catch (error: Throwable) {
        networkAuthLog(
            "auth failure path=$path type=${error::class.simpleName} " +
                "message=${error.message}",
        )
        throw error
    }
}
