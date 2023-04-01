package com.example.gpt.data.repository.image

import com.example.gpt.data.model.image.CreateImageRequest
import com.example.gpt.data.model.image.CreateImageResponse
import kotlinx.coroutines.flow.Flow

interface ImageRepository {

    fun getImages(createImageRequest: CreateImageRequest): Flow<CreateImageResponse>
}