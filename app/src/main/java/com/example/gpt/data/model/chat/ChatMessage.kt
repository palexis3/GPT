package com.example.gpt.data.model.chat

data class ChatMessage(
    val role: String,
    val content: String
)

fun ChatMessage.toChatMessageUi(): ChatMessageUi {
    return ChatMessageUi(
        role = this.role,
        content = this.content
    ).apply {
        // Note: Since this was a saved response, there's no need to
        // apply the typing animation again because the user has viewed it already.
        typeWriterSeenAlready = true
    }
}
