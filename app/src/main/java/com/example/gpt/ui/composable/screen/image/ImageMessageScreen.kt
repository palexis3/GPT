package com.example.gpt.ui.composable.screen.image

import android.content.Context
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
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
<<<<<<< HEAD:app/src/main/java/com/example/gpt/ui/composable/screen/image/ImageMessageScreen.kt
import androidx.compose.ui.unit.dp
=======
import androidx.compose.ui.unit.sp
>>>>>>> main:app/src/main/java/com/example/gpt/ui/composable/screen/ImageMessageScreen.kt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gpt.R
<<<<<<< HEAD:app/src/main/java/com/example/gpt/ui/composable/screen/image/ImageMessageScreen.kt
import com.example.gpt.ui.composable.ShowErrorCard
import com.example.gpt.ui.composable.ShowImage
import com.example.gpt.ui.composable.ShowLoading
import com.example.gpt.ui.theme.MediumPadding
=======
import com.example.gpt.ui.composable.ShowMessageContent
import com.example.gpt.ui.composable.ShowLoading
import com.example.gpt.ui.theme.EIGHT_DP
import com.example.gpt.ui.theme.FOUR_DP
import com.example.gpt.ui.theme.SIX_DP
import com.example.gpt.ui.theme.TWELVE_DP
>>>>>>> main:app/src/main/java/com/example/gpt/ui/composable/screen/ImageMessageScreen.kt
import com.example.gpt.ui.viewmodel.ImageMessageUiState
import com.example.gpt.ui.viewmodel.ImageViewModel

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class,
)
@Composable
fun ImageMessageScreen(
    imageViewModel: ImageViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var query by remember { mutableStateOf("") }
    var savedQuery by remember { mutableStateOf("") }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedNum by remember { mutableStateOf(1) }

    val imageMessageUiState: ImageMessageUiState by imageViewModel.imageCreateMessageUiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(TWELVE_DP),
        verticalArrangement = Arrangement.Top
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RectangleShape
                    )
                    .padding(TWELVE_DP)
                    .weight(1f),
                value = query,
                textStyle = MaterialTheme.typography.titleMedium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                onValueChange = { query = it },
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent),
                placeholder = {
                    Text(
                        stringResource(id = R.string.image_prompt),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.DarkGray,
                        fontStyle = FontStyle.Italic
                    )
                },
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
        }

<<<<<<< HEAD:app/src/main/java/com/example/gpt/ui/composable/screen/image/ImageMessageScreen.kt
        Row(
            modifier = Modifier.padding(MediumPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (query.trim().isNotEmpty()) {
                ExposedDropdownMenuBox(
                    modifier = Modifier
                        .width(100.dp)
                        .weight(1f),
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                ) {
                    TextField(
                        modifier = Modifier
                            .menuAnchor(),
                        value = selectedNum.toString(),
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text(
                                stringResource(id = R.string.num_of_image),
                                fontWeight = FontWeight.Bold
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
=======
        if (query.trim().isNotEmpty()) {
            ExposedDropdownMenuBox(
                modifier = Modifier.align(alignment = Alignment.End),
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor(),
                    value = selectedNum.toString(),
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            stringResource(id = R.string.num_of_image),
                            fontWeight = FontWeight.Bold
>>>>>>> main:app/src/main/java/com/example/gpt/ui/composable/screen/ImageMessageScreen.kt
                        )
                    },
                    shape = RoundedCornerShape(EIGHT_DP),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    (1..10).forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = item.toString()) },
                            onClick = {
                                selectedNum = item
                                dropdownExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(Modifier.height(SIX_DP))

<<<<<<< HEAD:app/src/main/java/com/example/gpt/ui/composable/screen/image/ImageMessageScreen.kt
                Button(
                    onClick = {
                        keyboardController?.hide()
                        imageViewModel.getCreateImages(query.trim(), selectedNum)
                        savedQuery = query
                        query = ""
                        imageViewModel.resetImageCreateUiFlow()
                    }
                ) {
                    Text(text = stringResource(id = R.string.search_image))
=======
            Button(
                modifier = Modifier.align(alignment = Alignment.End),
                onClick = {
                    keyboardController?.hide()
                    imageViewModel.getImages(query.trim(), selectedNum)
                    savedQuery = query
                    query = ""
                    imageViewModel.resetImageUiFlow()
>>>>>>> main:app/src/main/java/com/example/gpt/ui/composable/screen/ImageMessageScreen.kt
                }
            ) {
                Text(text = stringResource(id = R.string.search_image))
            }
        }

        Spacer(Modifier.height(TWELVE_DP))

        ImageListState(
            context,
            imageMessageUiState,
            savedQuery
        )
    }
}

@Composable
fun ImageListState(
    context: Context,
    imageMessageUiState: ImageMessageUiState,
    savedQuery: String
) {
    LazyColumn(
        verticalArrangement = Arrangement.SpaceEvenly,
        contentPadding = PaddingValues(
            top = TWELVE_DP,
            bottom = TWELVE_DP
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
                    ShowQueryText(context, text = savedQuery)
                }
                item {
<<<<<<< HEAD:app/src/main/java/com/example/gpt/ui/composable/screen/image/ImageMessageScreen.kt
                   ShowErrorCard()
=======
                    ShowMessageContent(
                        text = stringResource(id = R.string.error),
                        modifier = Modifier.padding(
                            start = TWELVE_DP,
                            top = FOUR_DP,
                            bottom = SIX_DP,
                            end = TWELVE_DP
                        ),
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 18.sp
                        )
                    )
>>>>>>> main:app/src/main/java/com/example/gpt/ui/composable/screen/ImageMessageScreen.kt
                }
            }
            is ImageMessageUiState.Success -> {
                item {
                    ShowQueryText(
                        context,
                        text = savedQuery
                    )
                }
                items(imageMessageUiState.imageMessageUi.images) { imageUrl ->
                    ShowImage(url = imageUrl)
                }
            }
        }
    }
}

@Composable
fun ShowQueryText(context: Context, text: String) {
    Text(
        text = context.getString(R.string.images_for, text),
        modifier = Modifier.padding(TWELVE_DP),
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        fontStyle = FontStyle.Italic
    )
}
<<<<<<< HEAD:app/src/main/java/com/example/gpt/ui/composable/screen/image/ImageMessageScreen.kt
=======

@Composable
fun ShowImage(url: String) {
    if (url.isNotEmpty()) {
        Card(
            modifier = Modifier
                .padding(top = FOUR_DP, start = TWELVE_DP, end = TWELVE_DP, bottom = FOUR_DP),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = EIGHT_DP
            ),
            shape = RoundedCornerShape(TWELVE_DP)
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
>>>>>>> main:app/src/main/java/com/example/gpt/ui/composable/screen/ImageMessageScreen.kt
