package com.example.gpt.data.repository.chat

import com.example.gpt.data.local.ChatDao
import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatMessage
import com.example.gpt.data.model.chat.ChatMessagesLocal
import com.example.gpt.data.model.chat.toChatMessages
import com.example.gpt.data.remote.OpenAIApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl @Inject constructor(
    private val api: OpenAIApi,
    private val dao: ChatDao
) : ChatRepository {

    // NOTE: To get all chat messages from local storage ->
    // ChatMessageLocal actually represents 2 ChatMessages (one representing what the user typed
    // in and the other representing the response from ChatGPT). So we must first convert the
    // ChatMessageLocal into two distinct ChatMessages and then flatMap all items
    override fun getAllSavedChatMessages(): Flow<List<ChatMessage>> =
        flow {
            dao
                .getAllLocalChatMessages()
                .map { list ->
                    list.flatMap { chatMessageLocal -> chatMessageLocal.toChatMessages() }
                }
                .collect {
                    emit(it)
                }
        }

    // NOTE: The reason seemingly easy API calls have to wrapped in a Flow builder is to
    // help setup the `asResult()` extension method that handles loading, error and success at
    // the view model layer.
    override fun getChatMessageFromApi(request: ChatCompletionRequest): Flow<ChatMessage> =
        flow {
            val response = api.createChatCompletion(request)
            val message = response.choices[0].message
            emit(message)
        }

    // NOTE: ChatMessagesLocal represents two chat messages so we only want the one that was
    // returned from the ChatGPT API. Or in other words, the messages with the "assistant" role
    override fun getSavedChatMessage(prompt: String): Flow<ChatMessage?> =
        flow {
            val message = dao.getLocalChatMessage(prompt).toChatMessages().find { chatMessage ->
                chatMessage.role == "assistant"
            }
            emit(message)
        }

    override suspend fun saveChatMessages(chatMessagesLocal: ChatMessagesLocal) {
        dao.insertLocalChatMessage(chatMessagesLocal)
    }
}
