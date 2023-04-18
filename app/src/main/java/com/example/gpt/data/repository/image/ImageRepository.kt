package com.example.gpt.data.repository.image

import com.example.gpt.data.model.image.create.ImageCreateRequest
import com.example.gpt.data.model.image.ImageMessage
import com.example.gpt.data.model.image.edit.ImageEditRequest
import kotlinx.coroutines.flow.Flow

interface ImageRepository {

    fun getImages(imageCreateRequest: ImageCreateRequest): Flow<ImageMessage>
    fun editImage(imageEditRequest: ImageEditRequest): Flow<ImageMessage>
}
