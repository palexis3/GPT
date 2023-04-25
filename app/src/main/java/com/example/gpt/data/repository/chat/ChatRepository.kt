package com.example.gpt.data.repository.chat

import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatMessage
import com.example.gpt.data.model.chat.ChatMessagesLocal
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllSavedChatMessages(): Flow<List<ChatMessage>>
    fun getChatMessageFromApi(request: ChatCompletionRequest): Flow<ChatMessage>
    fun getSavedChatMessage(prompt: String): Flow<ChatMessage?>
    suspend fun saveChatMessages(chatMessagesLocal: ChatMessagesLocal)
}
