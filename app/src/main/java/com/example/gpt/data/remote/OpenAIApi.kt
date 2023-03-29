package com.example.gpt.data.remote

import com.example.gpt.data.model.audio.CreateAudioRequest
import com.example.gpt.data.model.audio.CreateAudioResponse
import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.image.CreateImageRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIApi {

    @POST("/chat/completions")
    suspend fun createChatCompletion(
        @Body chatCompletionRequest: ChatCompletionRequest
    ): ChatCompletionRequest

    @POST("/images/generations")
    suspend fun createImage(
        @Body createImageRequest: CreateImageRequest
    ): CreateImageRequest

    @POST("/audio/transcriptions")
    suspend fun createTranscription(
        @Body createAudioRequest: CreateAudioRequest
    ): CreateAudioResponse
}
