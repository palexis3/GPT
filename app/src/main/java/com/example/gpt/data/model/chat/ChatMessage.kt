package com.example.gpt.data.model.chat

import com.example.gpt.data.model.Message

data class ChatMessage(
    val role: String,
    val content: String
) : Message
