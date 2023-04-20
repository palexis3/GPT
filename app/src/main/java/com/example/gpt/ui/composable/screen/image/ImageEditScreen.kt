package com.example.gpt.ui.composable.screen.image

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.Image
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.gpt.R
import com.example.gpt.ui.composable.ShowErrorCard
import com.example.gpt.ui.composable.ShowImage
import com.example.gpt.ui.composable.ShowLoading
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.ui.viewmodel.ImageMessageUiState
import com.example.gpt.ui.viewmodel.ImageViewModel
import com.example.gpt.utils.ComposeFileProvider
import com.example.gpt.utils.toBase64String

@OptIn(ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ImageEditScreen(
    imageViewModel: ImageViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    val imageMessageUiState by imageViewModel.imageEditMessageUiState.collectAsStateWithLifecycle()

    var prompt by remember { mutableStateOf("") }
    var numOf by remember { mutableStateOf(1) }

    var imageUri by remember { mutableStateOf<Uri>(Uri.EMPTY) }
    var imageBase64String by remember { mutableStateOf("") }

    val permissionCheckResult = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = {}
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { success ->
            if (success) {
                takePictureLauncher(context, cameraLauncher) { uri ->
                    imageUri = uri
                    imageBase64String = uri.toBase64String(context)
                    Log.d("XXX-ImageEditScreen", "permissionLauncher imageUri: $imageUri imageBase64String: $imageBase64String")
                }
            } else {
                Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { imagePickedUri -> }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MediumPadding)
    ) {
        TextField(
            value = prompt,
            onValueChange = { value ->
                prompt = value
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = {
                Text(
                    stringResource(id = R.string.image_edit_prompt),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.DarkGray,
                    fontStyle = FontStyle.Italic
                )
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = { /*TODO*/ },
                shape = RectangleShape
            ) {
                Text(stringResource(id = R.string.upload_image))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        takePictureLauncher(context, cameraLauncher) { uri ->
                            imageUri = uri
                            imageBase64String = uri.toBase64String(context)
                            Log.d("XXX-ImageEditScreen", "Button onClick imageUri: $imageUri imageBase64String: $imageBase64String")
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                shape = RectangleShape
            ) {
                Text(stringResource(id = R.string.take_photo))
            }
        }

        if (imageBase64String.isNotEmpty()) {
            Text(
                text = stringResource(id = R.string.image_selected),
                style = MaterialTheme.typography.labelMedium,
                fontStyle = FontStyle.Italic
            )
        }

        if (imageUri != Uri.EMPTY) {
            Log.d("XXX-ImageEditScreen", "Show AsyncImage uri: $imageUri")
            AsyncImage(
                model = imageUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentDescription = "$imageUri image",
                contentScale = ContentScale.Crop,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            // TODO: Add numOf selection
            Button(
                enabled = prompt.isNotEmpty() && imageBase64String.isNotEmpty(),
                onClick = {
                    keyboardController?.hide()
                    imageViewModel.getEditImage(
                        prompt = prompt,
                        numOf = numOf,
                        imageBase64String = imageBase64String
                    )
                    imageViewModel.resetImageEditUiFlow()
                }
            ) {
                Text(text = stringResource(id = R.string.search))
            }
        }

        ShowImageMessageUiState(imageMessageUiState)
    }
}

@Composable
fun ShowImageMessageUiState(imageMessageUiState: ImageMessageUiState) {
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
                    ShowErrorCard()
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

fun takePictureLauncher(
    context: Context,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    pictureSelected: (Uri) -> Unit
) {
    val fileUri = ComposeFileProvider.getFilUri(context)
    pictureSelected(fileUri)
    cameraLauncher.launch(fileUri)
}

fun selectImageFromFolderLauncher(
    context: Context,
    imageSelected: (String) -> Unit
) {

}
