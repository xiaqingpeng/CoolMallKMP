package com.joker.coolmall.core.model.response

import kotlinx.serialization.Serializable

@Serializable
data class NetworkPageMeta(
    val total: Int? = null,
    val size: Int? = null,
    val page: Int? = null,
)
