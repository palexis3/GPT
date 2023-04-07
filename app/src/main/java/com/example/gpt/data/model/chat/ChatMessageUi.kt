package com.example.gpt.data.model.chat

import com.example.gpt.data.model.MessageUi

data class ChatMessageUi(
    val role: String,
    val content: String
) : MessageUi {
    var typeWriterSeenAlready : Boolean = false
}