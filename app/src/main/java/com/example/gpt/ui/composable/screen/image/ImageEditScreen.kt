package com.example.gpt.ui.composable.screen.image

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.gpt.R
import com.example.gpt.ui.composable.ShowErrorCard
import com.example.gpt.ui.composable.ShowImage
import com.example.gpt.ui.composable.ShowLoading
import com.example.gpt.ui.theme.MediumPadding
import com.example.gpt.ui.viewmodel.ImageMessageUiState
import com.example.gpt.ui.viewmodel.ImageViewModel
import com.example.gpt.utils.bitmapToFile
import com.example.gpt.utils.convertUriToFile
import com.example.gpt.utils.createImageFile
import com.example.gpt.utils.fileTooLargeOrNull
import com.example.gpt.utils.getFileUri
import com.example.gpt.utils.processUriToFile
import com.example.gpt.utils.toBase64String
import com.example.gpt.utils.toMaskedBitmap
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.resolution
import id.zelory.compressor.constraint.size
import java.io.File
import kotlinx.coroutines.launch

private val MAX_FILE_SIZE = 4_194_304L

@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ImageEditScreen(
    imageViewModel: ImageViewModel = hiltViewModel()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var prompt by remember { mutableStateOf("") }
    val numOf by remember { mutableStateOf(1) }
    var currentImageUri by remember { mutableStateOf(Uri.EMPTY) }
    var tempImageUri by remember { mutableStateOf(Uri.EMPTY) }

    var currentImageFile by remember { mutableStateOf<File?>(null) }
    var compressedImageFile by remember { mutableStateOf<File?>(null) }

    var showImageErrorDialog by remember { mutableStateOf(false) }

    val imageMessageUiState by imageViewModel.imageEditMessageUiState.collectAsStateWithLifecycle()

    val permissionCheckResult = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    )

    val compressedFileFunc: () -> Unit = {
        coroutineScope.launch {
//            val maskedBitmap = currentImageFile?.toMaskedBitmap()
//            val tempFile = context.bitmapToFile(maskedBitmap)

            compressedImageFile = currentImageFile?.let {
                Compressor.compress(context, it) {
                    format(Bitmap.CompressFormat.PNG)
                    size(maxFileSize = MAX_FILE_SIZE)
                    resolution(1024, 1024)
                }
            }
        }
    }

    val fileFunc: () -> Unit = {
        coroutineScope.launch {
            currentImageFile = processUriToFile(currentImageUri, context)
            val maskedBitmap = currentImageFile.toMaskedBitmap()
            compressedImageFile = bitmapToFile(currentImageFile, maskedBitmap)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            currentImageUri = tempImageUri
            fileFunc.invoke()
//            currentImageFile = context.convertUriToFile(currentImageUri)
//            compressedFileFunc.invoke()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { imagePickedUri ->
            currentImageUri = imagePickedUri
//            currentImageFile = context.convertUriToFile(currentImageUri)
//            compressedFileFunc.invoke()
            fileFunc.invoke()
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { success ->
            if (success) {
                takePictureListener(context, cameraLauncher) { uri ->
                    tempImageUri = uri
                }
            } else {
                Toast.makeText(context, R.string.permission_denied, Toast.LENGTH_SHORT).show()
            }
        }
    )

    if (showImageErrorDialog) {
        ShowImageErrorDialog(
            errorMessage = stringResource(id = R.string.image_too_large)
        ) {
            showImageErrorDialog = !showImageErrorDialog
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(MediumPadding),
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                onClick = { imagePickerLauncher.launch("image/png") },
                shape = RectangleShape
            ) {
                Text(stringResource(id = R.string.upload_image))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Button(
                onClick = {
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        takePictureListener(context, cameraLauncher) { uri ->
                            tempImageUri = uri
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

        Spacer(Modifier.height(4.dp))

        if (currentImageUri != Uri.EMPTY) {
            AsyncImage(
                model = currentImageUri,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentDescription = "selected image",
                contentScale = ContentScale.Crop,
            )
        }

        Spacer(Modifier.height(4.dp))

        OutlinedTextField(
            value = prompt,
            onValueChange = { value ->
                prompt = value
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            placeholder = {
                Text(
                    stringResource(id = R.string.image_edit_prompt),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.LightGray,
                    fontStyle = FontStyle.Italic
                )
            }
        )

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            // TODO: Add numOf selection
            Button(
                enabled = prompt.isNotEmpty() && currentImageFile != null,
                onClick = {
                    keyboardController?.hide()

                    if (currentImageFile?.fileTooLargeOrNull() == true) {
                        showImageErrorDialog = true
                        return@Button
                    }

                    val compressedMaskFile = compressedImageFile ?: return@Button
                    val imageFile = currentImageFile ?: return@Button

                    imageViewModel.getEditImage(
                        prompt = prompt,
                        numOf = numOf,
                        file = imageFile,
                        mask = compressedMaskFile,
                        imageFileString = imageFile.toBase64String(),
                        maskFileString = compressedImageFile.toBase64String()
                    )

                    currentImageUri = Uri.EMPTY
                    currentImageFile = null
                    compressedImageFile = null
                    prompt = ""
                }
            ) {
                Text(text = stringResource(id = R.string.search))
            }
        }

        ShowImageMessageUiState(imageMessageUiState)
    }
}

@Composable
fun ShowImageErrorDialog(
    errorMessage: String,
    onDismissRequested: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismissRequested.invoke() },
        text = {
            Text(errorMessage)
        },
        confirmButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(MediumPadding),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = {
                    onDismissRequested.invoke()
                }) {
                    Text(stringResource(id = R.string.dismiss))
                }
            }
        }
    )
}

@Composable
fun ShowImageMessageUiState(imageMessageUiState: ImageMessageUiState) {
    // TODO - Add prompt and image that user entered in along with API response in chat fashion
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

fun takePictureListener(
    context: Context,
    cameraLauncher: ManagedActivityResultLauncher<Uri, Boolean>,
    pictureSelected: (Uri) -> Unit
) {
    val file = context.createImageFile()
    val fileUri = context.getFileUri(file)
    pictureSelected(fileUri)
    cameraLauncher.launch(fileUri)
}
