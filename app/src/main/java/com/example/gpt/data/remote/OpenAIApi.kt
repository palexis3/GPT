package com.example.gpt.data.remote

<<<<<<< HEAD
import com.example.gpt.data.model.audio.AudioCreateRequest
import com.example.gpt.data.model.audio.AudioCreateResponse
import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatCompletionResponse
import com.example.gpt.data.model.image.create.ImageCreateRequest
import com.example.gpt.data.model.image.create.ImageCreateResponse
import com.example.gpt.data.model.image.edit.ImageEditRequest
import com.example.gpt.data.model.image.edit.ImageEditResponse
import okhttp3.MultipartBody
=======
import com.example.gpt.data.model.audio.AudioCreateResponse
import com.example.gpt.data.model.chat.ChatCompletionRequest
import com.example.gpt.data.model.chat.ChatCompletionResponse
import com.example.gpt.data.model.image.ImageCreateRequest
import com.example.gpt.data.model.image.ImageCreateResponse
>>>>>>> main
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap

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
<<<<<<< HEAD
        @Body audioCreateRequest: AudioCreateRequest
    ): AudioCreateResponse

    @POST("/v1/images/edits")
    suspend fun editImage(
        @Body body: RequestBody
    ): ImageEditResponse

    @Multipart
    @POST("/v1/images/edits")
    suspend fun editImage(
        @PartMap partMap: MutableMap<String, RequestBody>,
        @Part image: MultipartBody.Part
    ): ImageEditResponse

    @Multipart
    @POST("/v1/images/edits")
    suspend fun editImage(
        @Part("prompt") prompt: RequestBody,
        @Part("n") n: RequestBody,
        @Part("response_format") responseFormat: RequestBody,
        @Part image: MultipartBody.Part
    ): ImageEditResponse
=======
        @Body body: RequestBody
    ): AudioCreateResponse

    @POST("/v1/audio/translations")
    suspend fun createTranslation(
        @Body body: RequestBody
    ): AudioCreateResponse
>>>>>>> main
}
