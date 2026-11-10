package com.joker.coolmall

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform