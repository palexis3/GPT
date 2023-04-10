package com.example.gpt.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
