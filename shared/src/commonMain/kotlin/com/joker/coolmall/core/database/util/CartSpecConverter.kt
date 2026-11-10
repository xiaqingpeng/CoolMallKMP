package com.joker.coolmall.core.database.util

import com.joker.coolmall.core.model.entity.CartGoodsSpec
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 购物车规格列表 ↔ JSON 字符串（在 Repository 层转换，不走 Room TypeConverter）。
 */
object CartSpecJson {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun encode(value: List<CartGoodsSpec>?): String {
        return if (value.isNullOrEmpty()) "[]" else json.encodeToString(value)
    }

    fun decode(value: String?): List<CartGoodsSpec> {
        if (value.isNullOrBlank() || value == "[]") return emptyList()
        return runCatching {
            json.decodeFromString<List<CartGoodsSpec>>(value)
        }.getOrElse { emptyList() }
    }
}
