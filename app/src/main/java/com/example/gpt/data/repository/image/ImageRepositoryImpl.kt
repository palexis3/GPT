package com.example.gpt.data.repository.image

import android.util.Log
import com.example.gpt.data.model.image.Image
import com.example.gpt.data.model.image.create.ImageCreateRequest
import com.example.gpt.data.model.image.ImageMessage
import com.example.gpt.data.model.image.ImageMessageUi
import com.example.gpt.data.model.image.edit.ImageEditRequest
import com.example.gpt.data.remote.OpenAIApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.parse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber

class ImageRepositoryImpl @Inject constructor(
    private val api: OpenAIApi
) : ImageRepository {

    override fun getImages(imageCreateRequest: ImageCreateRequest): Flow<ImageMessageUi> =
        flow {
            val response = api.createImage(imageCreateRequest)
            val messageUi = processImageResponse(response.data)
            emit(messageUi)
        }

    override fun editImage(imageEditRequest: ImageEditRequest): Flow<ImageMessageUi> =
        flow {
            // Attempt: 1
//            val formRequest: RequestBody = MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("prompt", imageEditRequest.prompt)
//                .addFormDataPart("n", imageEditRequest.n.toString())
//                .addFormDataPart(
//                    name = "image", filename = imageEditRequest.imageFile.name,
//                    body = imageEditRequest.imageFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                )
//                .addFormDataPart(
//                    name = "mask", filename = imageEditRequest.maskFile.name,
//                    body = imageEditRequest.maskFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                )
//                .build()
//
//            val response = api.editImage(formRequest)

            // Attempt: 2
//            val prompt = imageEditRequest.prompt.toRequestBody("text/plain".toMediaTypeOrNull())
//            val n = imageEditRequest.n.toString().toRequestBody("text/plain".toMediaTypeOrNull())
//            val responseFormat = "url".toRequestBody("text/plain".toMediaTypeOrNull())
//            val mask = MultipartBody.Part.createFormData(
//                name = "mask", filename = imageEditRequest.maskFile.name,
//                body = imageEditRequest.maskFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//            )
//            val image = MultipartBody.Part.createFormData(
//                name = "image", filename = imageEditRequest.maskFile.name,
//                body = imageEditRequest.maskFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//            )
//
//            val response = api.editImage(
//                prompt = prompt,
//                n = n,
//                responseFormat = responseFormat,
//                image = image
//            )


            // Attempt: 3
//            val map: MutableMap<String, RequestBody> = mutableMapOf()
//            map["prompt"] = prompt
//            map["n"] = n

//            val formRequest: RequestBody = MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("prompt", imageEditRequest.prompt)
//                .addFormDataPart("n", imageEditRequest.n.toString())
//                .build()

//            val response = api.editImage(
//                map,
//                image
//            )

            // Attempt: 4 - Try converting image and mask to b64_json and using that
            val formRequest: RequestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("prompt", imageEditRequest.prompt)
                .addFormDataPart("n", imageEditRequest.n.toString())
                .addFormDataPart("image", imageEditRequest.imageFileAsString)
                .build()

            val response = api.editImage(formRequest)

            Timber.tag("XXX-ImageRepository").d("image: %s", imageEditRequest.imageFileAsString)

            Timber.tag("XXX-ImageRepository").d("response: %s", response)
            val messageUi = processImageResponse(response.data)
            emit(messageUi)
        }

    private fun processImageResponse(data: List<Image>): ImageMessageUi {
        val images = data.map { image -> image.url }
        return ImageMessageUi(images)
    }
}
