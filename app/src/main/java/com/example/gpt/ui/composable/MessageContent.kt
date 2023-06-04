package com.example.gpt.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

// Note: ShowMessageContent is a wrapper around TypeWriter to show all types of
// responses ranging from error text and successful responses
@Composable
fun ShowMessageContent(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    contentSeenAlready: Boolean = false,
    onAnimationEnd: (() -> Unit)? = null
) {
    TypeWriter(
        text = text,
        modifier = modifier,
        textStyle = textStyle,
        onAnimationEnd = onAnimationEnd,
        typeWriterSeenAlready = contentSeenAlready
    )
}
