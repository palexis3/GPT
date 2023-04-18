package com.example.gpt.data.model.audio

data class AudioCreateRequest(
    val file: String,
    val model: String = "whisper-1"
)
