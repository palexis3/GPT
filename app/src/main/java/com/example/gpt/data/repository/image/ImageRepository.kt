package com.example.gpt.data.repository.image

<<<<<<< HEAD
import com.example.gpt.data.model.image.create.ImageCreateRequest
import com.example.gpt.data.model.image.ImageMessageUi
import com.example.gpt.data.model.image.edit.ImageEditRequest
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getImages(imageCreateRequest: ImageCreateRequest): Flow<ImageMessageUi>
    fun editImage(imageEditRequest: ImageEditRequest): Flow<ImageMessageUi>
=======
import com.example.gpt.data.model.image.ImageCreateRequest
import com.example.gpt.data.model.image.ImageMessage
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    fun getImages(imageCreateRequest: ImageCreateRequest): Flow<ImageMessage>
>>>>>>> main
}
