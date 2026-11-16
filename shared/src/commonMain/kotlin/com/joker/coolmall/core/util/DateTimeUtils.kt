package com.joker.coolmallkmp.core.util

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 跨平台日期工具：用 epoch 毫秒桥接 `kotlin.time.Clock` 与 `kotlinx.datetime`，
 * 避免两套 Instant 类型直接互操作冲突。
 */
object DateTimeUtils {

    /**
     * 判断 `endTime` 是否已过期。
     *
     * 支持：
     * - `yyyy-MM-dd`
     * - `yyyy-MM-dd HH:mm:ss` / `yyyy-MM-dd'T'HH:mm:ss`（取日期前缀）
     *
     * 规则：到期日当天仍有效（`endDate < today` 才算过期），对齐 CoolMallKotlin 粗粒度语义。
     */
    @OptIn(ExperimentalTime::class)
    fun isDateExpired(endTime: String?): Boolean {
        if (endTime.isNullOrBlank()) return false
        val datePart = endTime.trim().take(10)
        if (datePart.length < 10 || datePart[4] != '-' || datePart[7] != '-') return false
        val endDate = runCatching { LocalDate.parse(datePart) }.getOrNull() ?: return false
        val today = currentLocalDate()
        return endDate < today
    }

    @OptIn(ExperimentalTime::class)
    fun currentLocalDate(): LocalDate {
        val epochMs = Clock.System.now().toEpochMilliseconds()
        return Instant.fromEpochMilliseconds(epochMs)
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    }

    /** 当天 0 点 epoch（系统时区），调试/测试用 */
    fun startOfDayEpochMs(date: LocalDate): Long =
        date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}
