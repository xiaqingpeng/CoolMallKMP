package com.joker.coolmallkmp.core.util

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object TimeUtils {
    fun formatIso(value: String?, pattern: String = "yyyy-MM-dd HH:mm"): String {
        if (value.isNullOrBlank()) return ""
        return runCatching {
            val instant = Instant.parse(value)
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            "${dateTime.date} ${dateTime.time.hour.toString().padStart(2, '0')}:" +
                dateTime.time.minute.toString().padStart(2, '0')
        }.getOrDefault(value)
    }
}
