package com.joker.coolmallkmp.feature.user.data

import com.joker.coolmallkmp.feature.user.model.Region

/**
 * 全国行政区划数据。分片存放，避免 JVM 单方法超过 64KB 限制。
 */
object RegionData {
    fun getProvinces(): List<Region> = listOf(
        regionDataPart01(),
        regionDataPart02(),
        regionDataPart03(),
        regionDataPart04(),
        regionDataPart05(),
        regionDataPart06(),
        regionDataPart07(),
        regionDataPart08(),
        regionDataPart09(),
        regionDataPart10(),
        regionDataPart11(),
        regionDataPart12(),
        regionDataPart13(),
        regionDataPart14(),
        regionDataPart15(),
        regionDataPart16(),
        regionDataPart17(),
        regionDataPart18(),
        regionDataPart19(),
        regionDataPart20(),
        regionDataPart21(),
        regionDataPart22(),
        regionDataPart23(),
        regionDataPart24(),
        regionDataPart25(),
        regionDataPart26(),
        regionDataPart27(),
        regionDataPart28(),
        regionDataPart29(),
        regionDataPart30(),
        regionDataPart31()
    )
}
