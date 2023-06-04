package com.example.gpt.data.model.audio

import java.io.File

data class AudioCreateRequest(
    val file: File,
    val model: String = "whisper-1"
)
