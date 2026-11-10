package com.joker.coolmall.core.config

/**
 * 跨平台运行时配置。
 * Android 可在 MainActivity 注入 BuildConfig；iOS 使用默认生产地址。
 */
object AppConfig {
    /** 业务 API 根路径，需以 / 结尾 */
    var baseUrl: String = "https://mall.dusksnow.top/app/"
        private set

    var isDebug: Boolean = true
        private set

    fun configure(
        baseUrl: String = this.baseUrl,
        isDebug: Boolean = this.isDebug,
    ) {
        this.baseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        this.isDebug = isDebug
    }
}
