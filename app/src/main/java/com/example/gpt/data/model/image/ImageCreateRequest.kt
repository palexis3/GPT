package com.example.gpt.data.model.image

data class ImageCreateRequest(
    val prompt: String,
    val n: Int,
    val size: String = "512x512"
)
