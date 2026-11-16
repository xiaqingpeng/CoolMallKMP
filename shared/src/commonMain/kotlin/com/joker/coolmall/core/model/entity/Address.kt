package com.joker.coolmallkmp.core.model.entity

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class Address(
    val id: Long = 0,
    val userId: Long = 0,
    val contact: String = "",
    val phone: String = "",
    val province: String = "",
    val city: String = "",
    val district: String = "",
    val address: String = "",
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    val isDefault: Boolean = false,
    val createTime: String? = null,
    val updateTime: String? = null,
) {
    fun fullRegion(): String = listOf(province, city, district)
        .filter { it.isNotBlank() }
        .joinToString(" ")

    fun displayLine(): String = listOf(fullRegion(), address)
        .filter { it.isNotBlank() }
        .joinToString(" ")
}
