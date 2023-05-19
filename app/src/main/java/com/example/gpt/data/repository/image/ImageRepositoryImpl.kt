package com.example.gpt.data.repository.image

import com.example.gpt.data.model.image.ImageCreateRequest
import com.example.gpt.data.model.image.ImageMessage
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
            val images = response.data.map { image -> image.url }
            val imagesMessage = ImageMessage(images)
            emit(imagesMessage)
        }
}
