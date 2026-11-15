package com.joker.coolmall.core.network

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.joker.coolmall.core.config.AppConfig
import com.joker.coolmall.core.di.AppContainerProvider
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp

actual fun platformHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient =
    HttpClient(OkHttp) {
        engine {
            if (AppConfig.isDebug) {
                val context = AppContainerProvider.appContext as? Context
                if (context != null) {
                    addInterceptor(ChuckerInterceptor.Builder(context).build())
                }
            }
        }
        config()
    }
