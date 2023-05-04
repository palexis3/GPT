package com.example.gpt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatMessage
import com.example.gpt.data.model.chat.ChatMessageUi
import com.example.gpt.data.repository.chat.ChatRepository
import com.example.gpt.utils.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.gpt.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val chatRepository: ChatRepository
) : ViewModel() {

    val initialChatMessagesUi: StateFlow<List<ChatMessageUi>> =
        chatRepository
            .getAllChatMessagesFromLocal()
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

        viewModelScope.launch(Dispatchers.IO) {
            chatRepository
                .getChatMessage(request)
                .asResult()
                .collect { result ->
                    val chatMessageUiState = when (result) {
                        is Result.Loading -> ChatMessageUiState.Loading
                        is Result.Error -> ChatMessageUiState.Error
                        is Result.Success -> {
                            val chatMessageUi: ChatMessageUi = result.data
                            ChatMessageUiState.Success(chatMessageUi)
                        }
                    }
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
