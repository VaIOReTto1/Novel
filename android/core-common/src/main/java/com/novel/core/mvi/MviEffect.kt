package com.novel.core.mvi

import java.util.UUID

interface MviEffect {
    val timestamp: Long get() = System.currentTimeMillis()
    val id: String get() = UUID.randomUUID().toString()
}
