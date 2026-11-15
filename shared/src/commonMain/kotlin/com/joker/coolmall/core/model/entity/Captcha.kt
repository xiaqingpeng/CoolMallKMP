package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Captcha(
    val data: String = "",
    val captchaId: String = "",
)
