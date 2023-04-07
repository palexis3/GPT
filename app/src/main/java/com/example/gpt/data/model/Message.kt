package com.example.gpt.data.model

import java.util.UUID

interface Message {
    val id : String
        get() = UUID.randomUUID().toString()
}

object LoadingMessage : Message
