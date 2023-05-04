package com.example.gpt.ui.composable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gpt.R
import com.example.gpt.data.model.LoadingMessageUi
import com.example.gpt.data.model.MessageUi
import com.example.gpt.data.model.chat.ChatMessageUi
import com.example.gpt.ui.composable.ShowCardHeader
import com.example.gpt.ui.composable.ShowLoading
import com.example.gpt.ui.composable.TypeWriter
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.ui.viewmodel.ChatMessageUiState
import com.example.gpt.ui.viewmodel.ChatViewModel
import com.example.gpt.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
)
@Composable
fun ChatMessageScreen(
    chatViewModel: ChatViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var inputString by remember { mutableStateOf("") }
    val messageList = remember { mutableStateListOf<MessageUi>() }

    val messageListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val chatMessageUiState by chatViewModel.chatMessageUiState.collectAsStateWithLifecycle()
    val initialChatMessagesUi by chatViewModel.initialChatMessagesUi.collectAsStateWithLifecycle()

    val isLastMessageSeen by remember {
        derivedStateOf {
            val layoutInfo = messageListState.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo

            if (layoutInfo.totalItemsCount == 0) {
                false
            } else {
                val lastVisibleItem = visibleItemsInfo.last()
                val viewportHeight = layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset

                lastVisibleItem.index + 1 == layoutInfo.totalItemsCount &&
                        lastVisibleItem.offset + lastVisibleItem.size <= viewportHeight
            }
        }
    }

    // Note: When screen has finished composition, we ensure that the last message
    // can be seen
    LaunchedEffect(!isLastMessageSeen, messageList.size) {
        coroutineScope.launch {
            messageListState.animateScrollToItem(messageList.size, 1)
        }
    }

    // Note: On launch, we show the saved chat messages if the user wants to see them and
    // whether there's messages to show
    LaunchedEffect(initialChatMessagesUi) {
        if (settingsViewModel.showAndSaveChatHistoryState.first()
            && initialChatMessagesUi.isNotEmpty()
            && messageList.isEmpty()
        ) {
            messageList.addAll(initialChatMessagesUi)
        }
    }

    // Note: Handles chat messages being entered in realtime
    LaunchedEffect(chatMessageUiState) {
        processChatMessageUi(
            chatMessageUiState,
            onProcessed = { chatMessage ->
                if (messageList.last() is LoadingMessageUi) {
                    messageList.removeLast()
                }
                messageList.add(chatMessage)
            },
            onLoading = {
                messageList.add(LoadingMessageUi)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MediumPadding),
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f),
            state = messageListState
        ) {
            items(
                items = messageList
            ) { message ->
                when (message) {
                    is ChatMessageUi -> ShowMessage(
                        chatMessageUi = message
                    )
                    is LoadingMessageUi -> ShowLoading()
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RectangleShape
                    )
                    .padding(MediumPadding)
                    .weight(1f),
                value = inputString,
                textStyle = MaterialTheme.typography.titleMedium,
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { inputString = it },
                placeholder = {
                    Text(
                        stringResource(id = R.string.chat_prompt),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.DarkGray,
                        fontStyle = FontStyle.Italic
                    )
                },
                trailingIcon = {
                    if (inputString.isNotEmpty()) {
                        IconButton(onClick = { inputString = "" }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = "Clear Icon"
                            )
                        }
                    }
                }
            )

            if (inputString.trim().isNotEmpty()) {
                IconButton(onClick = {
                    val chatMessage = ChatMessageUi(role = "user", inputString)
                    messageList.add(chatMessage)
                    keyboardController?.hide()
                    chatViewModel.getChatMessage(inputString.trim())
                    inputString = ""
                    chatViewModel.resetMessageUiFlow()
                }) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}

private fun processChatMessageUi(
    chatMessageUiState: ChatMessageUiState,
    onProcessed: (ChatMessageUi) -> Unit,
    onLoading: () -> Unit
) {
    when (chatMessageUiState) {
        is ChatMessageUiState.Success -> {
            onProcessed(chatMessageUiState.chatMessageUi)
        }
        ChatMessageUiState.Error -> {
            onProcessed(ChatMessageUi(role = "error", ""))
        }
        ChatMessageUiState.Loading -> onLoading()
        else -> {}
    }
}

@Composable
fun ShowMessage(
    chatMessageUi: ChatMessageUi
) {
    val isUser = chatMessageUi.role == "user"
    val isChatAssistant = chatMessageUi.role == "assistant"
    val isErrorMessage = chatMessageUi.role == "error"

    val cardBackgroundColor = when {
        isUser -> MaterialTheme.colorScheme.primaryContainer
        isChatAssistant -> MaterialTheme.colorScheme.secondaryContainer
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
            modifier = Modifier
                .widthIn(200.dp, 275.dp)
                .padding(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardBackgroundColor
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            val message = if (isErrorMessage) {
                stringResource(id = R.string.error)
            } else {
                chatMessageUi.content
            }

            Column {
                ShowCardHeader(
                    text = when {
                        isChatAssistant || isErrorMessage -> stringResource(id = R.string.gpt)
                        else -> stringResource(id = R.string.you)
                    }
                )

                Spacer(modifier = Modifier.height(2.dp))

                if (isChatAssistant || isErrorMessage) {
                    TypeWriter(
                        text = message,
                        modifier = Modifier.padding(
                            start = 12.dp,
                            top = 4.dp,
                            bottom = 6.dp,
                            end = 12.dp
                        ),
                        textStyle = TextStyle(
                            color = when {
                                isChatAssistant -> MaterialTheme.colorScheme.secondary
                                else -> MaterialTheme.colorScheme.error
                            },
                            fontSize = 18.sp
                        ),
                        onAnimationEnd = {
                            chatMessageUi.typeWriterSeenAlready = true
                        },
                        typeWriterSeenAlready = chatMessageUi.typeWriterSeenAlready
                    )
                } else {
                    Text(
                        text = message,
                        modifier = Modifier.padding(
                            start = 12.dp,
                            top = 4.dp,
                            bottom = 6.dp,
                            end = 12.dp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}
