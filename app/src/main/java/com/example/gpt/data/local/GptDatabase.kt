package com.example.gpt.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.gpt.data.model.chat.ChatMessagesLocal

@Database(entities = [ChatMessagesLocal::class], version = 1)
abstract class GptDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}
