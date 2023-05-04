package com.example.gpt.data.model.chat

data class ChatMessage(
    val role: String,
    val content: String
)

fun ChatMessage.toChatMessageUi(): ChatMessageUi {
    return ChatMessageUi(
        role = this.role,
        content = this.content
    )
}
