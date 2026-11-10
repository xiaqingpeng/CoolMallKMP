package com.joker.coolmall

import android.app.Application
import android.content.Context
import com.joker.coolmall.core.di.AppContainerProvider
import com.joker.coolmall.core.platform.CurrentActivityHolder

/**
 * Android 入口初始化（由 androidApp MainActivity 调用）。
 */
fun initializeCoolMallAndroid(
    context: Context,
    baseUrl: String = "https://mall.dusksnow.top/app/",
    isDebug: Boolean = true,
) {
    val app = context.applicationContext as Application
    CurrentActivityHolder.install(app)
    AppContainerProvider.initialize(
        context = app,
        baseUrl = baseUrl,
        isDebug = isDebug,
    )
}
