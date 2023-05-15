package com.example.gpt.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.gpt.ui.theme.MediumPadding

// Note: ShowMessageContentCard is a wrapper around TypeWriter to show all types of
// responses ranging from error text and successful responses
@Composable
fun ShowMessageContentCard(
    text: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle(),
    contentSeenAlready: Boolean = false,
    onAnimationEnd: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.padding(MediumPadding),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        TypeWriter(
            text = text,
            modifier = modifier,
            textStyle = textStyle,
            onAnimationEnd = onAnimationEnd,
            typeWriterSeenAlready = contentSeenAlready
        )
    }
}
