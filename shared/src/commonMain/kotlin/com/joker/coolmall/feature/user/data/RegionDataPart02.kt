package com.joker.coolmallkmp.feature.user.data

import com.joker.coolmallkmp.feature.user.model.Region

// Province data is split into files to keep JVM methods below the 64KB limit.
internal fun regionDataPart02() : Region {
    return Region(
        id = "12" ,
        name = "天津市" ,
        children = listOf(
            Region(
                id = "1201" ,
                name = "天津市" ,
                children = listOf(
                    Region(
                        id = "120101" ,
                        name = "和平区"
                    ) ,
                    Region(
                        id = "120102" ,
                        name = "河东区"
                    ) ,
                    Region(
                        id = "120103" ,
                        name = "河西区"
                    ) ,
                    Region(
                        id = "120104" ,
                        name = "南开区"
                    ) ,
                    Region(
                        id = "120105" ,
                        name = "河北区"
                    ) ,
                    Region(
                        id = "120106" ,
                        name = "红桥区"
                    ) ,
                    Region(
                        id = "120110" ,
                        name = "东丽区"
                    ) ,
                    Region(
                        id = "120111" ,
                        name = "西青区"
                    ) ,
                    Region(
                        id = "120112" ,
                        name = "津南区"
                    ) ,
                    Region(
                        id = "120113" ,
                        name = "北辰区"
                    ) ,
                    Region(
                        id = "120114" ,
                        name = "武清区"
                    ) ,
                    Region(
                        id = "120115" ,
                        name = "宝坻区"
                    ) ,
                    Region(
                        id = "120116" ,
                        name = "滨海新区"
                    ) ,
                    Region(
                        id = "120117" ,
                        name = "宁河区"
                    ) ,
                    Region(
                        id = "120118" ,
                        name = "静海区"
                    ) ,
                    Region(
                        id = "120119" ,
                        name = "蓟州区"
                    )
                )
            )
        )
    )
}
