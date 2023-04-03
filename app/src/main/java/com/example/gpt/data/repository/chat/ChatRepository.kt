package com.example.gpt.data.repository.chat

import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {

    fun getChatMessage(request: ChatCompletionRequest): Flow<ChatMessage>
}
