package com.example.gpt.data.repository.image

import com.example.gpt.data.model.image.Image
import com.example.gpt.data.model.image.create.ImageCreateRequest
import com.example.gpt.data.model.image.ImageMessage
import com.example.gpt.data.model.image.edit.ImageEditRequest
import com.example.gpt.data.remote.OpenAIApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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
            val response = api.editImage(imageEditRequest)
            val message = processImageResponse(response.data)
            emit(message)
        }

    private fun processImageResponse(data: List<Image>) : ImageMessage {
        val images = data.map { image -> image.url }
        return ImageMessage(images)
    }
}
