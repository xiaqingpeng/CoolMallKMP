package com.joker.coolmall.core.network

import com.joker.coolmall.core.config.AppConfig
import com.joker.coolmall.core.session.AuthSession
import com.joker.coolmall.core.session.networkAuthLog
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.takeFrom
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

val NetworkJson = Json {
    ignoreUnknownKeys = true
    explicitNulls = false
    encodeDefaults = true
    isLenient = true
    coerceInputValues = true
}

object Network {
    val httpClient: HttpClient by lazy { createHttpClient() }

    /**
     * 图片下载专用客户端：不带业务 Authorization，避免 CDN 403。
     */
    val imageHttpClient: HttpClient by lazy { createImageHttpClient() }
}

expect fun platformHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient

private fun createHttpClient(): HttpClient = platformHttpClient {
    install(DefaultRequest) {
        url.takeFrom(AppConfig.baseUrl)
        val token = AuthSession.token.trim()
        if (token.isNotBlank()) {
            headers[HttpHeaders.Authorization] = token
        }
        networkAuthLog(
            "default request baseUrl=${AppConfig.baseUrl} authorization=${AuthSession.tokenSummary()}",
        )
    }
    install(ContentNegotiation) {
        json(NetworkJson)
    }
}

private fun createImageHttpClient(): HttpClient = platformHttpClient {
    install(DefaultRequest) {
        header("User-Agent", "CoolMallKMP/1.0")
        header("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8")
    }
}
