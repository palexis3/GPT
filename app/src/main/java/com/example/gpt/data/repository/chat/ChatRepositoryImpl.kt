package com.example.gpt.data.repository.chat

import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatMessage
import com.example.gpt.data.remote.OpenAIApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ChatRepositoryImpl @Inject constructor(
    private val api: OpenAIApi
) : ChatRepository {

    override fun getChatMessage(request: ChatCompletionRequest): Flow<ChatMessage> =
        flow {
            val response = api.createChatCompletion(request)
            val message = response.choices[0].message
            emit(message)
        }
}
