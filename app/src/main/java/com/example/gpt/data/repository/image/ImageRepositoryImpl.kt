package com.example.gpt.data.repository.image

import android.util.Log
import com.example.gpt.data.model.image.Image
import com.example.gpt.data.model.image.create.ImageCreateRequest
import com.example.gpt.data.model.image.ImageMessage
import com.example.gpt.data.model.image.edit.ImageEditRequest
import com.example.gpt.data.remote.OpenAIApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber

class ImageRepositoryImpl @Inject constructor(
    private val api: OpenAIApi
) : ImageRepository {

    override fun getImages(imageCreateRequest: ImageCreateRequest): Flow<ImageMessage> =
        flow {
            val response = api.createImage(imageCreateRequest)
            val message = processImageResponse(response.data)
            emit(message)
        }

    override fun editImage(imageEditRequest: ImageEditRequest): Flow<ImageMessage> =
        flow {
            val formRequest: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("prompt", imageEditRequest.prompt)
                .addFormDataPart("n", imageEditRequest.n.toString())
                .addFormDataPart(
                    name = "image", filename = imageEditRequest.imageFile.name,
                    body = imageEditRequest.imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
                .addFormDataPart(
                    name = "mask", filename = imageEditRequest.imageFile.name,
                    body = imageEditRequest.imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
                .build()

//                .addFormDataPart("image", imageEditRequest.imageFileAsString)

            val response = api.editImage(formRequest)
            Timber.tag("XXX-ImageRepository").d("response: %s", response)
            val message = processImageResponse(response.data)
            emit(message)
        }

    private fun processImageResponse(data: List<Image>): ImageMessage {
        val images = data.map { image -> image.url }
        return ImageMessage(images)
    }
}
