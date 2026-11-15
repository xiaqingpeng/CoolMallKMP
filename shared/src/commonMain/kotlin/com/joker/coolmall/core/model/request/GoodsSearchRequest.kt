package com.joker.coolmall.core.model.request

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class GoodsSearchRequest(
    val page: Int = 1,
    val size: Int = 20,
    val typeId: List<Long>? = null,
    val minPrice: String? = null,
    val maxPrice: String? = null,
    @SerialName("keyWord")
    val keyWord: String? = null,
    val order: String? = null,
    val sort: String? = null,
    val recommend: Boolean = false,
    val featured: Boolean = false,
)
