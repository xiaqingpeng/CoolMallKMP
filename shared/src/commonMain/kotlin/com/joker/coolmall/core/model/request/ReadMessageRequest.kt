package com.joker.coolmall.core.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ReadMessageRequest(
    val ids: List<Long>,
)
