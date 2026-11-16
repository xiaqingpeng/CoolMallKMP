package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.Serializable

@Serializable
data class Goods(
    val id: Long = 0,
    val typeId: Long = 0,
    val title: String = "",
    val subTitle: String? = null,
    val mainPic: String = "",
    val pics: List<String>? = null,
    val price: Int = 0,
    val sold: Int = 0,
    val content: String? = null,
    val contentPics: List<String>? = null,
    val recommend: Boolean = false,
    val featured: Boolean = false,
    val status: Int = 0,
    val sortNum: Int = 0,
    val specs: List<GoodsSpec>? = null,
    val createTime: String? = null,
    val updateTime: String? = null,
)
