package com.example.gpt.data.repository.image

import com.example.gpt.data.model.image.CreateImageRequest
import com.example.gpt.data.model.image.CreateImageResponse
import com.example.gpt.data.remote.OpenAIApi
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ImageRepositoryImpl @Inject constructor(
    private val api: OpenAIApi
) : ImageRepository {

    override fun getImages(createImageRequest: CreateImageRequest): Flow<CreateImageResponse> =
        flow {
            val response = api.createImage(createImageRequest)
            emit(response)
        }
}
