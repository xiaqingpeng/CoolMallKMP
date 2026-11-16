package com.joker.coolmallkmp.core.model.request

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class PageRequest(
    @EncodeDefault
    val page: Int = 1,
    @EncodeDefault
    val size: Int = 10,
    @EncodeDefault
    val sort: String = "desc",
)
