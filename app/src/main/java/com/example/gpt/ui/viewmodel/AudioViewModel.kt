package com.example.gpt.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.gpt.data.model.audio.AudioMessageUi
import com.example.gpt.data.repository.audio.AudioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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

    private val _audioMessageUiState = MutableStateFlow<AudioMessageUiState>(AudioMessageUiState.Uninitialized)
    val audioMessageUiState
        get() = _audioMessageUiState.asStateFlow()

}
