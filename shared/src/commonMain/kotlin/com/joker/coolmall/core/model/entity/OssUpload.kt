package com.joker.coolmall.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class TencentCredentials(
    val tmpSecretId: String? = null,
    val tmpSecretKey: String? = null,
    val sessionToken: String? = null,
)

@Serializable
data class OssUpload(
    val credentials: TencentCredentials? = null,
    val requestId: String? = null,
    val expiration: String? = null,
    val startTime: Long? = null,
    val expiredTime: Long? = null,
    val url: String? = null,
)
