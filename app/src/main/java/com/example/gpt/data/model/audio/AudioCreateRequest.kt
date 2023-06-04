package com.example.gpt.data.model.audio

<<<<<<< HEAD:app/src/main/java/com/example/gpt/data/model/audio/AudioCreateRequest.kt
data class AudioCreateRequest(
    val file: String,
=======
import java.io.File

data class AudioCreateRequest(
    val file: File,
>>>>>>> main:app/src/main/java/com/example/gpt/data/model/audio/CreateAudioRequest.kt
    val model: String = "whisper-1"
)
