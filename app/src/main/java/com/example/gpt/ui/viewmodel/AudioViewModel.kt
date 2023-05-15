package com.example.gpt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gpt.data.model.audio.AudioCreateRequest
import com.example.gpt.data.model.audio.AudioMessageUi
import com.example.gpt.data.repository.audio.AudioRepository
import com.example.gpt.utils.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.gpt.utils.Result
import kotlinx.coroutines.flow.update

sealed interface AudioMessageUiState {
    object Loading : AudioMessageUiState
    object Error : AudioMessageUiState
    object Uninitialized : AudioMessageUiState
    data class Success(val audioMessageUi: AudioMessageUi) : AudioMessageUiState
}

@HiltViewModel
class AudioViewModel @Inject constructor(
    private val audioRepository: AudioRepository
) : ViewModel() {

    private val _audioMessageUiState =
        MutableStateFlow<AudioMessageUiState>(AudioMessageUiState.Uninitialized)
    val audioMessageUiState
        get() = _audioMessageUiState.asStateFlow()

    fun createTranscription(audioFile: File) {
        val request = AudioCreateRequest(file = audioFile)

        viewModelScope.launch(Dispatchers.IO) {
            audioRepository
                .getTranscription(request)
                .asResult()
                .collect { result ->
                    val audioMessageUiState = when (result) {
                        is Result.Loading -> AudioMessageUiState.Loading
                        is Result.Error -> AudioMessageUiState.Error
                        is Result.Success -> {
                            val audioMessageUi = result.data
                            AudioMessageUiState.Success(audioMessageUi)
                        }
                    }

                    _audioMessageUiState.update { audioMessageUiState }
                }
        }
    }
}
