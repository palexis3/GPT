package com.example.gpt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatMessage
import com.example.gpt.data.model.chat.ChatMessageUi
import com.example.gpt.data.model.chat.ChatMessagesLocal
import com.example.gpt.data.model.chat.toChatMessageUi
import com.example.gpt.data.repository.chat.ChatRepository
import com.example.gpt.utils.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.gpt.utils.Result
import com.example.gpt.utils.SettingPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ChatMessageUiState {
    object Loading : ChatMessageUiState
    object Error : ChatMessageUiState
    object Uninitialized : ChatMessageUiState
    data class Success(val chatMessageUi: ChatMessageUi) : ChatMessageUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val settingPreferences: SettingPreferences
) : ViewModel() {

    // Note: Offline mode and Show history are the two functionalities being supported
    val initialChatMessagesUi: StateFlow<List<ChatMessageUi>> =
        chatRepository
            .getAllSavedChatMessages()
            .map { list ->
                list.map { chatMessage -> chatMessage.toChatMessageUi() }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = emptyList()
            )

    private val _chatMessageUiState =
        MutableStateFlow<ChatMessageUiState>(ChatMessageUiState.Uninitialized)
    val chatMessageUiState
        get() = _chatMessageUiState.asStateFlow()

    fun getChatMessage(message: String, temperature: Double = 1.0) {
        val request = ChatCompletionRequest(
            messages = listOf(ChatMessage(role = "user", content = message)),
            temperature = temperature
        )

        viewModelScope.launch {
            /**
             * TODO: Combine getChatMessageFromApi and getSavedChatMessage to first see if a
             * response for the prompt was fetched and stored already else make another fetch to API
             */
            chatRepository
                .getChatMessageFromApi(request)
                .asResult()
                .collect { result ->
                    val chatMessageUiState = when (result) {
                        is Result.Loading -> ChatMessageUiState.Loading
                        is Result.Error -> ChatMessageUiState.Error
                        is Result.Success -> {
                            val data = result.data
                            val chatMessageUi =
                                ChatMessageUi(role = data.role, content = data.content)

                            // NOTE: Save successful response for this prompt typed in if setting
                            // preferences have been turned on.
                            if (settingPreferences.saveAndShowChatHistoryState().first()) {
                                chatRepository.saveChatMessages(ChatMessagesLocal(prompt = message, content = data.content))
                            }
                            ChatMessageUiState.Success(chatMessageUi)
                        }
                    }
                    delay(100L)
                    _chatMessageUiState.update { chatMessageUiState }
                }
        }
    }

    fun resetMessageUiFlow() {
        viewModelScope.launch {
            _chatMessageUiState.update { ChatMessageUiState.Uninitialized }
        }
    }
}
