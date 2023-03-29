package com.example.gpt.data.model.audio

data class CreateAudioRequest(
    val file: String,
    val model: String = "whisper-1"
)
