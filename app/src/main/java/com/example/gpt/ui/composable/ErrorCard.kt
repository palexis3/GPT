package com.example.gpt.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gpt.R
import com.example.gpt.ui.theme.MediumPadding

@Composable
fun ShowErrorCard() {
    Card(
        modifier = Modifier.padding(MediumPadding),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        TypeWriter(
            text = stringResource(id = R.string.error),
            modifier = Modifier.padding(
                start = 12.dp,
                top = 4.dp,
                bottom = 6.dp,
                end = 12.dp
            ),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.error,
                fontSize = 18.sp
            )
        )
    }
}
