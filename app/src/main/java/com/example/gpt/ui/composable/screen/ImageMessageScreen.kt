package com.example.gpt.ui.composable.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Search
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.gpt.R
import com.example.gpt.ui.composable.ShowLoading
import com.example.gpt.ui.composable.TypeWriter
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.ui.viewmodel.ImageMessageUiState
import com.example.gpt.ui.viewmodel.ImageViewModel

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalLifecycleComposeApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ImageMessageScreen(
    imageViewModel: ImageViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var query by remember { mutableStateOf("") }

    val imageMessageUiState: ImageMessageUiState by imageViewModel.imageMessageUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top
    ) {
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
                value = query,
                textStyle = MaterialTheme.typography.titleMedium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { query = it },
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                placeholder = { Text(
                    stringResource(id = R.string.search_image),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.DarkGray,
                    fontStyle = FontStyle.Italic
                ) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                tint = MaterialTheme.colorScheme.onBackground,
                                contentDescription = "Clear icon"
                            )
                        }
                    }
                }
            )

            if (query.trim().isNotEmpty()) {
                IconButton(
                    onClick = {
                        keyboardController?.hide()
                        imageViewModel.getImages(query.trim())
                        query = ""
                        imageViewModel.resetImageUiFlow()
                    }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        tint = MaterialTheme.colorScheme.onBackground,
                        contentDescription = stringResource(id = R.string.send)
                    )
                }
            }
        }

        Spacer(Modifier.height(6.dp))

        ImageListState(imageMessageUiState)
    }
}

@Composable
fun ImageListState(imageMessageUiState: ImageMessageUiState) {
    LazyColumn(
        verticalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(
            top = MediumPadding,
            bottom = MediumPadding
        )
    ) {
        when (imageMessageUiState) {
            is ImageMessageUiState.Uninitialized -> {}
            is ImageMessageUiState.Loading -> {
                item {
                    ShowLoading(
                        horizontalAlignment = Alignment.Start
                    )
                }
            }
            is ImageMessageUiState.Error -> {
                item {
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
            is ImageMessageUiState.Success -> {
                items(imageMessageUiState.imageMessageUi.images) { imageUrl ->
                    ShowImage(url = imageUrl)
                }
            }
        }
    }
}

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
