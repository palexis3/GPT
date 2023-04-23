package com.example.gpt.data.model.image.edit

import java.io.File

data class ImageEditRequest(
    val prompt: String,
    val n: Int,
    val imageFile: File,
    val imageFileAsString: String
)
