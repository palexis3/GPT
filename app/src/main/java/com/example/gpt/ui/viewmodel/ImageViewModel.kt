package com.example.gpt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpt.data.model.image.CreateImageRequest
import com.example.gpt.data.model.image.ImageMessageUi
import com.example.gpt.data.repository.image.ImageRepository
import com.example.gpt.utils.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.gpt.utils.Result
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

    private val _imageMessageUiState =
        MutableStateFlow<ImageMessageUiState>(ImageMessageUiState.Uninitialized)
    val imageMessageUiState
        get() = _imageMessageUiState.asStateFlow()

    fun getImages(prompt: String, numOf: Int = 1) {
        val request = CreateImageRequest(
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
                            val data = result.data
                            val imageMessageUi = ImageMessageUi(images = data.images)
                            ImageMessageUiState.Success(imageMessageUi)
                        }
                    }
                    _imageMessageUiState.update { imageMessageUiState }
                }
        }
    }

    fun resetImageUiFlow() {
        viewModelScope.launch {
            _imageMessageUiState.update { ImageMessageUiState.Uninitialized }
        }
    }
}
