package com.example.gpt.ui.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ShowImage(url: String) {
    if (url.isNotEmpty()) {
        Card(
            modifier = Modifier
                .padding(top = 4.dp, start = 12.dp, end = 12.dp, bottom = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            AsyncImage(
                model = url,
                modifier = Modifier.fillMaxSize(),
                contentDescription = "GPT Image",
                contentScale = ContentScale.Crop
            )
        }
    }
}
