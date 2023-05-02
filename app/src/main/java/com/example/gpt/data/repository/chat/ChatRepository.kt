package com.example.gpt.data.repository.chat

import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatMessageUi
import com.example.gpt.data.model.chat.ChatMessagesLocal
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllChatMessagesFromLocal(): Flow<List<ChatMessageUi>>
    fun getChatMessageFromLocal(prompt: String): Flow<ChatMessageUi?>
    fun getChatMessage(request: ChatCompletionRequest): Flow<ChatMessageUi>
    suspend fun saveChatMessages(chatMessagesLocal: ChatMessagesLocal)
}
