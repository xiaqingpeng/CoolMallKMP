package com.joker.coolmallkmp.feature.user.data

import com.joker.coolmallkmp.feature.user.model.Region

// Province data is split into files to keep JVM methods below the 64KB limit.
internal fun regionDataPart01() : Region {
    return Region(
        id = "11" ,
        name = "北京市" ,
        children = listOf(
            Region(
                id = "1101" ,
                name = "北京市" ,
                children = listOf(
                    Region(
                        id = "110101" ,
                        name = "东城区"
                    ) ,
                    Region(
                        id = "110102" ,
                        name = "西城区"
                    ) ,
                    Region(
                        id = "110105" ,
                        name = "朝阳区"
                    ) ,
                    Region(
                        id = "110106" ,
                        name = "丰台区"
                    ) ,
                    Region(
                        id = "110107" ,
                        name = "石景山区"
                    ) ,
                    Region(
                        id = "110108" ,
                        name = "海淀区"
                    ) ,
                    Region(
                        id = "110109" ,
                        name = "门头沟区"
                    ) ,
                    Region(
                        id = "110111" ,
                        name = "房山区"
                    ) ,
                    Region(
                        id = "110112" ,
                        name = "通州区"
                    ) ,
                    Region(
                        id = "110113" ,
                        name = "顺义区"
                    ) ,
                    Region(
                        id = "110114" ,
                        name = "昌平区"
                    ) ,
                    Region(
                        id = "110115" ,
                        name = "大兴区"
                    ) ,
                    Region(
                        id = "110116" ,
                        name = "怀柔区"
                    ) ,
                    Region(
                        id = "110117" ,
                        name = "平谷区"
                    ) ,
                    Region(
                        id = "110118" ,
                        name = "密云区"
                    ) ,
                    Region(
                        id = "110119" ,
                        name = "延庆区"
                    )
                )
            )
        )
    )
}
