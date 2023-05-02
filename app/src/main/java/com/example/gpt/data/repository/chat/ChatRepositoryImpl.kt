package com.example.gpt.data.repository.chat

import com.example.gpt.data.local.ChatDao
import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatMessageUi
import com.example.gpt.data.model.chat.ChatMessagesLocal
import com.example.gpt.data.model.chat.toChatMessageUi
import com.example.gpt.data.model.chat.toChatMessagesUi
import com.example.gpt.data.remote.OpenAIApi
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class ChatRepositoryImpl @Inject constructor(
    private val api: OpenAIApi,
    private val dao: ChatDao
) : ChatRepository {

    // NOTE: To get all chat messages from local storage ->
    // ChatMessageLocal actually represents 2 ChatMessages (one representing what the user typed
    // in and the other representing the response from ChatGPT). So we must first convert the
    // ChatMessageLocal into two distinct ChatMessageUi and then flatMap all items
    override fun getAllChatMessagesFromLocal(): Flow<List<ChatMessageUi>> =
        flow {
            dao
                .getAllLocalChatMessages()
                .map { list ->
                    list.flatMap { chatMessageLocal -> chatMessageLocal.toChatMessagesUi() }
                }
                .collect {
                    emit(it)
                }
        }

    // Note: `getChatMessage` uses an offline approach where we first try to fetch the
    // locally saved chat message for a particular prompt otherwise in the case null is returned,
    // we fetch from the API
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getChatMessage(request: ChatCompletionRequest): Flow<ChatMessageUi> {
        val prompt = request.messages[0].content

        return getChatMessageFromLocal(prompt)
            .flatMapLatest { localChatMessageUi: ChatMessageUi? ->
                flow {
                    if (localChatMessageUi == null) {
                        val response = api.createChatCompletion(request)
                        val chatMessage = response.choices[0].message
                        val chatMessageUi = chatMessage.toChatMessageUi()
                        emit(chatMessageUi)
                    } else {
                        emit(localChatMessageUi)
                    }
                }
            }
    }

    // NOTE: ChatMessagesLocal represents two chat messages so we only want the one that was
    // returned from the ChatGPT API. Or in other words, the messages with the "assistant" role
    override fun getChatMessageFromLocal(prompt: String): Flow<ChatMessageUi?> =
        flow {
            val localChatMessageUi =
                dao.getLocalChatMessage(prompt).toChatMessagesUi().find { chatMessage ->
                    chatMessage.role == "assistant"
                }
            emit(localChatMessageUi)
        }

    override suspend fun saveChatMessages(chatMessagesLocal: ChatMessagesLocal) {
        dao.insertLocalChatMessage(chatMessagesLocal)
    }
}
