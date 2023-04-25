package com.example.gpt.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.gpt.data.model.chat.ChatMessagesLocal
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chatMessagesLocal")
    fun getAllLocalChatMessages(): Flow<List<ChatMessagesLocal>>

    @Query(value = "SELECT * FROM chatMessagesLocal WHERE prompt = :prompt")
    fun getLocalChatMessage(prompt: String): ChatMessagesLocal

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalChatMessage(message: ChatMessagesLocal)
}
