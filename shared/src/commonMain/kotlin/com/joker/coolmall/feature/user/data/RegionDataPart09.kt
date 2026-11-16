package com.joker.coolmallkmp.feature.user.data

import com.joker.coolmallkmp.feature.user.model.Region

// Province data is split into files to keep JVM methods below the 64KB limit.
internal fun regionDataPart09(): Region {
    return             Region(
                id = "31",
                name = "上海市",
                children = listOf(
                    Region(
                        id = "3101",
                        name = "上海市",
                        children = listOf(
                            Region(
                                id = "310101",
                                name = "黄浦区"
                            ),
                            Region(
                                id = "310104",
                                name = "徐汇区"
                            ),
                            Region(
                                id = "310105",
                                name = "长宁区"
                            ),
                            Region(
                                id = "310106",
                                name = "静安区"
                            ),
                            Region(
                                id = "310107",
                                name = "普陀区"
                            ),
                            Region(
                                id = "310109",
                                name = "虹口区"
                            ),
                            Region(
                                id = "310110",
                                name = "杨浦区"
                            ),
                            Region(
                                id = "310112",
                                name = "闵行区"
                            ),
                            Region(
                                id = "310113",
                                name = "宝山区"
                            ),
                            Region(
                                id = "310114",
                                name = "嘉定区"
                            ),
                            Region(
                                id = "310115",
                                name = "浦东新区"
                            ),
                            Region(
                                id = "310116",
                                name = "金山区"
                            ),
                            Region(
                                id = "310117",
                                name = "松江区"
                            ),
                            Region(
                                id = "310118",
                                name = "青浦区"
                            ),
                            Region(
                                id = "310120",
                                name = "奉贤区"
                            ),
                            Region(
                                id = "310151",
                                name = "崇明区"
                            )
                        )
                    )
                )
            )
}
