package com.example.gpt.utils

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun TypeWriter(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    typistSpeed: Long = 5L,
    onAnimationEnd: (() -> Unit)? = null
) {
    val currentText = remember {
        mutableStateOf("")
    }
    val remainingText = remember {
        mutableStateOf(text)
    }

    LaunchedEffect(key1 = true) {
        while (remainingText.value.isNotEmpty()) {
            currentText.value += remainingText.value.first()
            remainingText.value = remainingText.value.removePrefix("${remainingText.value.first()}")
            delay(typistSpeed)
        }
        if (remainingText.value.isEmpty()) {
            onAnimationEnd?.invoke()
        }
    }

    Row(
        modifier = modifier
            .animateContentSize()
            .padding(end = 5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = currentText.value,
            style = textStyle,
        )
    }
}
