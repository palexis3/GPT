package com.example.gpt.data.remote

import com.example.gpt.data.model.audio.CreateAudioRequest
import com.example.gpt.data.model.audio.CreateAudioResponse
import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatCompletionResponse
import com.example.gpt.data.model.image.CreateImageRequest
import com.example.gpt.data.model.image.CreateImageResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIApi {

    @POST("/v1/chat/completions")
    suspend fun createChatCompletion(
        @Body chatCompletionRequest: ChatCompletionRequest
    ): ChatCompletionResponse

    @POST("/v1/images/generations")
    suspend fun createImage(
        @Body createImageRequest: CreateImageRequest
    ): CreateImageResponse

    @POST("/v1/audio/transcriptions")
    suspend fun createTranscription(
        @Body createAudioRequest: CreateAudioRequest
    ): CreateAudioResponse
}
