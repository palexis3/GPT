package com.example.gpt.data.remote

import com.example.gpt.data.model.audio.AudioCreateRequest
import com.example.gpt.data.model.audio.AudioCreateResponse
import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatCompletionResponse
import com.example.gpt.data.model.image.create.ImageCreateRequest
import com.example.gpt.data.model.image.create.ImageCreateResponse
import com.example.gpt.data.model.image.edit.ImageEditRequest
import com.example.gpt.data.model.image.edit.ImageEditResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface OpenAIApi {

    @POST("/v1/chat/completions")
    suspend fun createChatCompletion(
        @Body chatCompletionRequest: ChatCompletionRequest
    ): ChatCompletionResponse

    @POST("/v1/images/generations")
    suspend fun createImage(
        @Body imageCreateRequest: ImageCreateRequest
    ): ImageCreateResponse

    @POST("/v1/audio/transcriptions")
    suspend fun createTranscription(
        @Body audioCreateRequest: AudioCreateRequest
    ): AudioCreateResponse

    @POST("/v1/images/edits")
    suspend fun editImage(
        @Body imageEditRequest: ImageEditRequest
    ): ImageEditResponse
}
