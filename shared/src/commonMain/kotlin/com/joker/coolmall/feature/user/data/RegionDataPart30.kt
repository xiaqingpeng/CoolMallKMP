package com.joker.coolmallkmp.feature.user.data

import com.joker.coolmallkmp.feature.user.model.Region

// Province data is split into files to keep JVM methods below the 64KB limit.
internal fun regionDataPart30(): Region {
    return             Region(
                id = "64",
                name = "宁夏回族自治区",
                children = listOf(
                    Region(
                        id = "6401",
                        name = "银川市",
                        children = listOf(
                            Region(
                                id = "640104",
                                name = "兴庆区"
                            ),
                            Region(
                                id = "640105",
                                name = "西夏区"
                            ),
                            Region(
                                id = "640106",
                                name = "金凤区"
                            ),
                            Region(
                                id = "640121",
                                name = "永宁县"
                            ),
                            Region(
                                id = "640122",
                                name = "贺兰县"
                            ),
                            Region(
                                id = "640181",
                                name = "灵武市"
                            )
                        )
                    ),
                    Region(
                        id = "6402",
                        name = "石嘴山市",
                        children = listOf(
                            Region(
                                id = "640202",
                                name = "大武口区"
                            ),
                            Region(
                                id = "640205",
                                name = "惠农区"
                            ),
                            Region(
                                id = "640221",
                                name = "平罗县"
                            )
                        )
                    ),
                    Region(
                        id = "6403",
                        name = "吴忠市",
                        children = listOf(
                            Region(
                                id = "640302",
                                name = "利通区"
                            ),
                            Region(
                                id = "640303",
                                name = "红寺堡区"
                            ),
                            Region(
                                id = "640323",
                                name = "盐池县"
                            ),
                            Region(
                                id = "640324",
                                name = "同心县"
                            ),
                            Region(
                                id = "640381",
                                name = "青铜峡市"
                            )
                        )
                    ),
                    Region(
                        id = "6404",
                        name = "固原市",
                        children = listOf(
                            Region(
                                id = "640402",
                                name = "原州区"
                            ),
                            Region(
                                id = "640422",
                                name = "西吉县"
                            ),
                            Region(
                                id = "640423",
                                name = "隆德县"
                            ),
                            Region(
                                id = "640424",
                                name = "泾源县"
                            ),
                            Region(
                                id = "640425",
                                name = "彭阳县"
                            )
                        )
                    ),
                    Region(
                        id = "6405",
                        name = "中卫市",
                        children = listOf(
                            Region(
                                id = "640502",
                                name = "沙坡头区"
                            ),
                            Region(
                                id = "640521",
                                name = "中宁县"
                            ),
                            Region(
                                id = "640522",
                                name = "海原县"
                            )
                        )
                    )
                )
            )
}
