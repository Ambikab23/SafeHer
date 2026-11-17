
package com.ambika.safeher.chat

import com.ambika.safeher.database.SafeHerDatabase

class ChatRepository(
    private val database: SafeHerDatabase
) {

    private val queries = database.chatMessageQueries

    fun getAllMessages(): List<ChatMessage> {
        return queries.getAllMessages().executeAsList().map { row ->
            ChatMessage(
                id = row.id,
                message = row.message,
                isSentByMe = row.isSentByMe != 0L,
                timestamp = row.timestamp
            )
        }
    }

    fun insertMessage(message: ChatMessage) {
        queries.insertMessage(
            message = message.message,
            isSentByMe = if (message.isSentByMe) 1 else 0,
            timestamp = message.timestamp
        )
    }

    fun deleteMessage(id: Long) {
        queries.deleteMessage(id)
    }

    fun deleteAllMessages() {
        queries.deleteAllMessages()
    }
}
