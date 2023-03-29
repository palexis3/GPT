package com.example.gpt.data.model.chat

data class ChatCompletionResponse(
    val id: String,
    val choices: List<ChoicesResponse>
)

data class ChoicesResponse(
    val finish_reason: String,
    val message: ChatMessage
)
