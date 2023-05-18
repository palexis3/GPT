package com.example.gpt.data.repository.audio

import com.example.gpt.data.model.audio.AudioCreateRequest
import com.example.gpt.data.model.audio.AudioMessageUi
import com.example.gpt.data.remote.OpenAIApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody

class AudioRepositoryImpl @Inject constructor(
    private val api: OpenAIApi
) : AudioRepository {

    override fun getTranscription(request: AudioCreateRequest): Flow<AudioMessageUi> = flow {
        val formRequest = getFormRequest(request)
        val response = api.createTranscription(formRequest)
        val audioMessageUi = AudioMessageUi(text = response.text)
        emit(audioMessageUi)
    }

    override fun getTranslation(request: AudioCreateRequest): Flow<AudioMessageUi> = flow {
        val formRequest = getFormRequest(request)
        val response = api.createTranslation(formRequest)
        val audioMessageUi = AudioMessageUi(text = response.text)
        emit(audioMessageUi)
    }

    private fun getFormRequest(request: AudioCreateRequest): RequestBody {
        return MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("model", request.model)
            .addFormDataPart(
                name = "file", filename = request.file.name,
                body = request.file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            )
            .build()
    }
}
