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

/**
 * Note: Since this was a saved response, there's no need to
 * apply the typing animation again because the user has viewed it already.
 */
fun ChatMessagesLocal.toChatMessagesUi(): List<ChatMessageUi> {
    val list = mutableListOf<ChatMessageUi>()
    list.add(ChatMessageUi(role = "user", content = this.prompt))
    list.add(ChatMessageUi(role = "assistant", content = this.content))
    list.forEach { chatMessageUi -> chatMessageUi.apply { typeWriterSeenAlready = true } }

    return list
}
