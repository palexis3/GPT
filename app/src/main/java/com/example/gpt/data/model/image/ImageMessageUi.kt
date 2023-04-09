package com.example.gpt.data.model.image

import com.example.gpt.data.model.MessageUi

data class ImageMessageUi(
    val images: List<String>
) : MessageUi
