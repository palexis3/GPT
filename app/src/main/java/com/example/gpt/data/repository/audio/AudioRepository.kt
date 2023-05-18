package com.example.gpt.data.repository.audio

import com.example.gpt.data.model.audio.AudioCreateRequest
import com.example.gpt.data.model.audio.AudioMessageUi
import kotlinx.coroutines.flow.Flow

interface AudioRepository {

    fun getTranscription(request: AudioCreateRequest) : Flow<AudioMessageUi>
    fun getTranslation(request: AudioCreateRequest) : Flow<AudioMessageUi>
}
