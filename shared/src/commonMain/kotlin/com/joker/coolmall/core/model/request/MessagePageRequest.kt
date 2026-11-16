package com.joker.coolmallkmp.core.model.request

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class MessagePageRequest(
    val sessionId: Long = 0,
    @EncodeDefault
    val page: Int = 1,
    @EncodeDefault
    val size: Int = 20,
    @EncodeDefault
    val order: String = "createTime",
    @EncodeDefault
    val sort: String = "desc",
)
