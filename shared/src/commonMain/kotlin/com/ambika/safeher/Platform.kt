package com.ambika.safeher

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform