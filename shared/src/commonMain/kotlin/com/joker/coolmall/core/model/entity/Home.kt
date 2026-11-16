package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val banner: List<Banner>? = null,
    val category: List<Category>? = null,
    val categoryAll: List<Category>? = null,
    val flashSale: List<Goods>? = null,
    val recommend: List<Goods>? = null,
    val coupon: List<Coupon>? = null,
    val goods: List<Goods>? = null,
)
