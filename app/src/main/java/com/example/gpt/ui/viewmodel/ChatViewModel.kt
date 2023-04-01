package com.example.gpt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatMessage
import com.example.gpt.data.repository.chat.ChatRepository
import com.example.gpt.utils.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.gpt.utils.Result
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ChatMessageUiState {
    object Loading : ChatMessageUiState
    object Error : ChatMessageUiState
    data class Success(val chatMessage: ChatMessage) : ChatMessageUiState
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _chatMessageUiState =
        MutableStateFlow<ChatMessageUiState>(ChatMessageUiState.Loading)
    val chatMessageUiState = _chatMessageUiState.asStateFlow()

    fun getChatMessage(message: String, temperature: Int = 1) {
        val request = ChatCompletionRequest(
            messages = listOf(ChatMessage(role = "user", content = message))
        )

        viewModelScope.launch {
            chatRepository
                .getChatMessage(request)
                .asResult()
                .collect { result ->
                    val chatMessageUiState = when (result) {
                        is Result.Loading -> ChatMessageUiState.Loading
                        is Result.Error -> ChatMessageUiState.Error
                        is Result.Success -> {
                            val data = result.data
                            ChatMessageUiState.Success(data)
                        }
                    }

                    _chatMessageUiState.update { chatMessageUiState }
                }
        }
    }
}