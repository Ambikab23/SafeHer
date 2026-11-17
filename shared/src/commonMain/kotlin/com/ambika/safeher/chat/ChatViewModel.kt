
package com.ambika.safeher.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    init {
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch(Dispatchers.Default) {
            _messages.value = repository.getAllMessages()
        }
    }

    fun sendMessage(message: String) {
        val trimmedMessage = message.trim()

        if (trimmedMessage.isBlank()) return

        viewModelScope.launch(Dispatchers.Default) {
            val newMessage = ChatMessage(
                id = 0L,
                message = trimmedMessage,
                isSentByMe = true,
                timestamp = "Now"
            )

            repository.insertMessage(newMessage)
            loadMessages()
        }
    }

    fun deleteMessage(id: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteMessage(id)
            loadMessages()
        }
    }

    fun deleteAllMessages() {
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteAllMessages()
            loadMessages()
        }
    }
}
