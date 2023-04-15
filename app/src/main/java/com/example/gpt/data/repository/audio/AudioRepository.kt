package com.example.gpt.data.repository.audio

import com.example.gpt.data.model.audio.AudioMessage
import com.example.gpt.data.model.audio.CreateAudioRequest
import kotlinx.coroutines.flow.Flow

interface AudioRepository {

    fun getTranscription(request: CreateAudioRequest) : Flow<AudioMessage>
}