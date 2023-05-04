package com.example.gpt.data.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GptDatabase =
        Room.databaseBuilder(
            context,
            GptDatabase::class.java,
            "gpt-database"
        ).build()

    @Provides
    @Singleton
    fun provideChatDao(gptDatabase: GptDatabase): ChatDao =
        gptDatabase.chatDao()
}
