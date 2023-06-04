package com.example.gpt.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.gpt.ui.theme.FOUR_DP
import com.example.gpt.ui.theme.TWELVE_DP

@Composable
fun ShowCardHeader(text: String) {
    Text(
        modifier = Modifier.padding(start = TWELVE_DP, top = FOUR_DP),
        text = text,
        color = MaterialTheme.colorScheme.tertiary,
        fontSize = 10.sp,
        fontStyle = FontStyle.Italic,
        fontWeight = FontWeight.SemiBold
    )
}
