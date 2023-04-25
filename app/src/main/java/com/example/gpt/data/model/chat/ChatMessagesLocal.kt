package com.example.gpt.data.model.chat

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Note: ChatMessageLocal is the data model that represents a table in the GptDatabase where
 * the `prompt` is generated from the user while `content` is the response from the ChatGPT API of
 * said prompt. So in essence, ChatMessageLocal model represents two chat messages (one that shows
 * the prompt the user typed in and the response they got from ChatGPT)
 */
@Entity
data class ChatMessagesLocal(
    @PrimaryKey val prompt: String,
    @ColumnInfo(name = "content") val content: String
)

fun ChatMessagesLocal.toChatMessages(): List<ChatMessage> {
    val list = mutableListOf<ChatMessage>()
    list.add(ChatMessage(role = "user", content = this.prompt))
    list.add(ChatMessage(role = "assistant", content = this.content))
    return list
}