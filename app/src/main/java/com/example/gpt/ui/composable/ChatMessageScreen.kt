package com.example.gpt.ui.composable

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gpt.R
import com.example.gpt.data.model.LoadingMessageUi
import com.example.gpt.data.model.MessageUi
import com.example.gpt.data.model.chat.ChatMessageUi
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.ui.viewmodel.ChatMessageUiState
import com.example.gpt.ui.viewmodel.ChatViewModel
import kotlinx.coroutines.launch


@OptIn(
    ExperimentalLifecycleComposeApi::class,
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun ChatMessageScreen(
    chatViewModel: ChatViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var inputString by remember { mutableStateOf("") }
    val messageList = remember { mutableStateListOf<MessageUi>() }

    val messageListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val chatMessageUiState: ChatMessageUiState by chatViewModel.chatMessageUiState.collectAsStateWithLifecycle()

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

    var scrollToPosition by remember { mutableStateOf(0F) }

    LaunchedEffect(messageList.size) {
        if (messageList.size > 0) {
            coroutineScope.launch {
                messageListState.animateScrollToItem(messageList.size - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(MediumPadding),
        verticalArrangement = Arrangement.Bottom
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .animateContentSize(),
            state = messageListState
        ) {
            items(
                items = messageList
            ) { message ->
                when (message) {
                    is ChatMessageUi -> ShowMessage(
                        chatMessageUi = message,
                        currentYPosition = { position -> },
                        dynamicMessageHeight = { heightChange -> }
                    )
                    is LoadingMessageUi -> ShowLoading()
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
                if (inputString.trim().isNotEmpty()) {
                    val chatMessage = ChatMessageUi(role = "user", inputString)
                    messageList.add(chatMessage)
                    keyboardController?.hide()
                    chatViewModel.getChatMessage(inputString.trim())
                    inputString = ""
                    chatViewModel.resetMessageUiFlow()
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
fun ShowLoading() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        Card(
            modifier = Modifier
                .width(200.dp)
                .padding(4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            ShowCardHeader(
                text = stringResource(id = R.string.gpt)
            )
            Spacer(modifier = Modifier.height(10.dp))
            DotsLoading(
                modifier = Modifier.padding(
                    start = 12.dp,
                    top = 8.dp,
                    bottom = 6.dp,
                    end = 12.dp
                )
            )
        }
    }
}

@Composable
fun ShowCardHeader(text: String) {
    Text(
        modifier = Modifier.padding(start = 12.dp, top = 4.dp),
        text = text,
        color = MaterialTheme.colorScheme.tertiary,
        fontSize = 10.sp,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
fun ShowMessage(
    chatMessageUi: ChatMessageUi,
    currentYPosition: (Float) -> Unit,
    dynamicMessageHeight: (Int) -> Unit
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
                .padding(4.dp)
                .onGloballyPositioned { layoutCoordinates ->
                    currentYPosition(layoutCoordinates.positionInParent().y)
                }
                .onSizeChanged { size ->
                    dynamicMessageHeight(size.height)
                },
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
