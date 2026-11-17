package com.ambika.safeher.chat

data class ChatMessage(
    val id: Long,
    val message: String,
    val isSentByMe: Boolean,
    val timestamp: String
)
