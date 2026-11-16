package com.joker.coolmallkmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform