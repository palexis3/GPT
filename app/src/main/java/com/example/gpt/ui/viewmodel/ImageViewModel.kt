package com.example.gpt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpt.data.model.image.create.ImageCreateRequest
import com.example.gpt.data.model.image.ImageMessageUi
import com.example.gpt.data.model.image.edit.ImageEditRequest
import com.example.gpt.data.repository.image.ImageRepository
import com.example.gpt.utils.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.gpt.utils.Result
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ImageMessageUiState {
    object Loading : ImageMessageUiState
    object Error : ImageMessageUiState
    object Uninitialized : ImageMessageUiState
    data class Success(val imageMessageUi: ImageMessageUi) : ImageMessageUiState
}

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : ViewModel() {

    private val _imageCreateMessageUiState =
        MutableStateFlow<ImageMessageUiState>(ImageMessageUiState.Uninitialized)
    val imageCreateMessageUiState
        get() = _imageCreateMessageUiState.asStateFlow()

    private val _imageEditMessageUiState =
        MutableStateFlow<ImageMessageUiState>(ImageMessageUiState.Uninitialized)
    val imageEditMessageUiState
        get() = _imageEditMessageUiState.asStateFlow()

    fun getCreateImages(prompt: String, numOf: Int = 1) {
        val request = ImageCreateRequest(
            prompt = prompt, n = numOf
        )

        viewModelScope.launch(Dispatchers.IO) {
            imageRepository
                .getImages(request)
                .asResult()
                .collect { result ->
                    val imageMessageUiState = when (result) {
                        is Result.Loading -> ImageMessageUiState.Loading
                        is Result.Error -> ImageMessageUiState.Error
                        is Result.Success -> {
                            val imageMessageUi = result.data
                            ImageMessageUiState.Success(imageMessageUi)
                        }
                    }
                    _imageCreateMessageUiState.update { imageMessageUiState }
                }
        }
    }

    fun getEditImage(
        prompt: String,
        numOf: Int = 1,
        file: File,
        mask: File,
        imageFileString: String?
    ) {
        val imageEditRequest = ImageEditRequest(
            prompt = prompt,
            n = numOf,
            imageFile = file,
            maskFile = mask,
            imageFileAsString = imageFileString ?: ""
        )

        viewModelScope.launch {
            imageRepository.editImage(imageEditRequest)
                .asResult()
                .collect { result ->
                    val imageMessageUiState = when (result) {
                        is Result.Loading -> ImageMessageUiState.Loading
                        is Result.Error -> ImageMessageUiState.Error
                        is Result.Success -> {
                            val imageMessageUi = result.data
                            ImageMessageUiState.Success(imageMessageUi)
                        }
                    }
                    _imageEditMessageUiState.update { imageMessageUiState }
                }
        }
    }

    fun resetImageCreateUiFlow() {
        viewModelScope.launch {
            _imageCreateMessageUiState.update { ImageMessageUiState.Uninitialized }
        }
    }
}
