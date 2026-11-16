package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Captcha(
    val data: String = "",
    val captchaId: String = "",
)
