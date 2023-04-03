package com.example.gpt.ui.composable

import android.widget.Space
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gpt.R
import com.example.gpt.data.model.LoadingMessage
import com.example.gpt.data.model.Message
import com.example.gpt.data.model.chat.ChatMessage
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.ui.viewmodel.ChatMessageUiState
import com.example.gpt.ui.viewmodel.ChatViewModel
import com.example.gpt.utils.DotsTyping
import com.example.gpt.utils.TypeWriter


@OptIn(
    ExperimentalLifecycleComposeApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun ChatMessageScreen(
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    var inputString by remember { mutableStateOf("") }
    val messageList = remember { mutableStateListOf<Message>() }

    val keyboardController = LocalSoftwareKeyboardController.current

    val chatMessageUiState: ChatMessageUiState by chatViewModel.chatMessageUiState.collectAsStateWithLifecycle(
        ChatMessageUiState.Uninitialized
    )

    processChatMessageUi(
        chatMessageUiState,
        onProcessed = { chatMessage ->
            if (messageList.last() is LoadingMessage) {
                messageList.removeLast()
            }
            messageList.add(chatMessage)
        },
        onLoading = {
            messageList.add(LoadingMessage)
        }
    )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MediumPadding),
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items = messageList) { message ->
                when (message) {
                    is ChatMessage -> ShowMessage(chatMessage = message)
                    LoadingMessage -> ShowLoading()
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            Modifier
                .fillMaxWidth(),
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
                    val chatMessage = ChatMessage(role = "user", inputString)
                    messageList.add(chatMessage)
                    keyboardController?.hide()
                    chatViewModel.getChatMessage(inputString)
//                    chatViewModel.resetChatMessageFlow()
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
    onProcessed: (ChatMessage) -> Unit,
    onLoading: () -> Unit
) {
    when (chatMessageUiState) {
        is ChatMessageUiState.Success -> {
            onProcessed(chatMessageUiState.chatMessage)
        }
        is ChatMessageUiState.Error -> {
            onProcessed(ChatMessage(role = "error", ""))
        }
        is ChatMessageUiState.Uninitialized -> {}
        else -> onLoading.invoke()
    }
}

@Composable
fun ShowLoading() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Card(
            modifier = Modifier.width(175.dp).weight(1f),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp).weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {

                DotsTyping()
            }
        }
    }
}

@Composable
fun ShowMessage(chatMessage: ChatMessage) {
    val isUser = chatMessage.role == "user"
    val isChatAssistant = chatMessage.role == "assistant"
    val isErrorMessage = chatMessage.role == "error"

    val backgroundColor = when {
        isUser -> MaterialTheme.colorScheme.primaryContainer
        isChatAssistant -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.errorContainer
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = when {
            isChatAssistant || isErrorMessage -> Alignment.End
            else -> Alignment.Start
        }
    ) {
        Card(
            modifier = Modifier.widthIn(250.dp, 300.dp),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 4.dp
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            val message = if (isErrorMessage) {
                stringResource(id = R.string.error)
            } else {
                chatMessage.content
            }

            Column {
                Text(
                    modifier = Modifier.padding(start = 12.dp, top = 4.dp),
                    text = when {
                        isChatAssistant || isErrorMessage -> stringResource(id = R.string.gpt)
                        else -> stringResource(id = R.string.you)
                    },
                    color = Color.Black,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (isChatAssistant || isErrorMessage) {
                    TypeWriter(
                        text = message,
                        modifier = Modifier.padding(12.dp),
                        textStyle = TextStyle(
                            color = when {
                                isChatAssistant -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.error
                            }
                        )
                    )
                } else {
                    Text(
                        text = message,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(4.dp))
}
