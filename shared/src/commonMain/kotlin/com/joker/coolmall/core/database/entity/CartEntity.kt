package com.joker.coolmallkmp.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * 购物车数据库实体。
 * [specJson] 存规格列表 JSON，避免 Room TypeConverter 对嵌套 List 的 KSP MissingType。
 */
@Entity(tableName = "carts")
data class CartEntity(
    @PrimaryKey
    val goodsId: Long,
    val goodsName: String,
    val goodsMainPic: String,
    val specJson: String = "[]",
)
