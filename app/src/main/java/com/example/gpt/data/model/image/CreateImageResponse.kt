package com.example.gpt.data.model.image

data class CreateImageResponse(
    val data: List<Image>
)

data class Image(
    val url: String
)
