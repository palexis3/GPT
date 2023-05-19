package com.example.gpt.data.repository.image

import com.example.gpt.data.model.image.ImageCreateRequest
import com.example.gpt.data.model.image.ImageMessage
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getImages(imageCreateRequest: ImageCreateRequest): Flow<ImageMessage>
}
