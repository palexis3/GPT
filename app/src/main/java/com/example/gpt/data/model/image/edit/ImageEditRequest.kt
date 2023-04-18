package com.example.gpt.data.model.image.edit

data class ImageEditRequest(
    val image: String,
    val prompt: String,
    val n: Int = 1
)