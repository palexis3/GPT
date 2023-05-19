package com.example.gpt.data.model.image

data class ImageCreateResponse(
    val data: List<Image>
)

data class Image(
    val url: String
)
