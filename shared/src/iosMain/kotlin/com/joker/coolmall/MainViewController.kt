package com.joker.coolmallkmp

import androidx.compose.ui.window.ComposeUIViewController
import com.joker.coolmallkmp.core.di.AppContainerProvider
import com.joker.coolmallkmp.ui.CoolMallApp

fun MainViewController() = ComposeUIViewController {
    AppContainerProvider.initialize(
        context = null,
        baseUrl = "https://mall.dusksnow.top/app/",
        isDebug = true,
    )
    CoolMallApp()
}
