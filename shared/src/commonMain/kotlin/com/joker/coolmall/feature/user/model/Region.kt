package com.joker.coolmall.feature.user.model

data class Region(
    val id: String,
    val name: String,
    val children: List<Region> = emptyList(),
)
