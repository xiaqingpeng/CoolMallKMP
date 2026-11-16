package com.joker.coolmallkmp.core.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * 浏览足迹实体（商品 ID 为主键，重复浏览覆盖 viewTime）
 */
@Entity(tableName = "footprints")
data class FootprintEntity(
    @PrimaryKey
    val goodsId: Long,
    val goodsName: String,
    val goodsSubTitle: String?,
    val goodsMainPic: String,
    val goodsPrice: Int,
    val goodsSold: Int,
    val viewTime: Long,
)
