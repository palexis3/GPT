package com.example.gpt.data.repository.chat

import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatMessage
import com.example.gpt.data.model.chat.ChatMessageUi
import com.example.gpt.data.model.chat.ChatMessagesLocal
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllSavedChatMessages(): Flow<List<ChatMessageUi>>
    fun getSavedChatMessage(prompt: String): Flow<ChatMessageUi?>
    fun getChatMessageFromApi(request: ChatCompletionRequest): Flow<ChatMessage>
    suspend fun saveChatMessages(chatMessagesLocal: ChatMessagesLocal)
}
