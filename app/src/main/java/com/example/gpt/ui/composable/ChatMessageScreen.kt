package com.example.gpt.ui.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gpt.R
import com.example.gpt.data.model.chat.ChatMessage
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.ui.viewmodel.ChatMessageUiState
import com.example.gpt.ui.viewmodel.ChatViewModel


@OptIn(ExperimentalLifecycleComposeApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun ChatMessageScreen(
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    var inputString by remember { mutableStateOf("") }
    val messageList = remember { mutableStateListOf<ChatMessage>() }

    val keyboardController = LocalSoftwareKeyboardController.current

    val chatMessageUiState: ChatMessageUiState by chatViewModel.chatMessageUiState.collectAsStateWithLifecycle()

    processChatMessageUi(
        chatMessageUiState = chatMessageUiState
    ) { chatMessage ->
        if (chatMessage != null) {
            messageList.add(chatMessage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MediumPadding),
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items = messageList) { message ->
                ShowMessage(chatMessage = message)
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextField(
                modifier = Modifier.weight(1f),
                value = inputString,
                onValueChange = { inputString = it },
                label = { Text(stringResource(id = R.string.send)) }
            )

            IconButton(onClick = {
                if (inputString.isNotEmpty()) {
                    val chatMessage = ChatMessage(role = "assistant", inputString)
                    messageList.add(chatMessage)
                    keyboardController?.hide()
                    chatViewModel.getChatMessage(inputString)
                    inputString = ""
                }
            }) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = stringResource(id = R.string.send)
                )
            }
        }
    }
}

fun processChatMessageUi(
    chatMessageUiState: ChatMessageUiState,
    onProcessed: (ChatMessage?) -> Unit
) {
    val chatMessage = when (chatMessageUiState) {
        is ChatMessageUiState.Success -> {
            chatMessageUiState.chatMessage
        }
        is ChatMessageUiState.Error -> {
            ChatMessage(role = "error", "")
        }
        else -> null
    }
    onProcessed(chatMessage)
}

@Composable
fun ShowMessage(chatMessage: ChatMessage) {
    val isUser = chatMessage.role == "user"
    val isChatAssistant = chatMessage.role == "assistant"
    val isErrorMessage = chatMessage.role == "error"

    var backgroundColor = if (isUser) {
      MaterialTheme.colorScheme.primaryContainer
    } else MaterialTheme.colorScheme.secondaryContainer

    backgroundColor = if (isErrorMessage) {
       MaterialTheme.colorScheme.errorContainer
    } else backgroundColor

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = when {
            isUser || isErrorMessage -> Alignment.End
            else -> Alignment.Start
        }
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            val message = if (isErrorMessage) {
                stringResource(id = R.string.error)
            } else {
                chatMessage.content
            }

            Text(
                text = message,
                modifier = Modifier.padding(8.dp),
                color = when {
                    isUser -> MaterialTheme.colorScheme.primary
                    isChatAssistant -> MaterialTheme.colorScheme.secondary
                    else -> MaterialTheme.colorScheme.error
                }
            )
        }
    }
    Spacer(Modifier.height(4.dp))
}
