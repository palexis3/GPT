package com.example.gpt.data.repository.audio

import com.example.gpt.data.model.audio.AudioMessage
import com.example.gpt.data.model.audio.CreateAudioRequest
import com.example.gpt.data.remote.OpenAIApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AudioRepositoryImpl @Inject constructor(
    private val api: OpenAIApi
) : AudioRepository {

    override fun getTranscription(request: CreateAudioRequest): Flow<AudioMessage> = flow {
        val response = api.createTranscription(request)
        val audioMessage = AudioMessage(text = response.text)
        emit(audioMessage)
    }
}