package com.joker.coolmallkmp.core.util

object ValidationUtils {
    private val phonePattern = Regex("^1[3-9]\\d{9}$")

    fun isPhone(value: String): Boolean = phonePattern.matches(value.trim())
    fun isNotBlank(value: String?): Boolean = !value.isNullOrBlank()
    fun isPassword(value: String): Boolean = value.length >= 6
}
