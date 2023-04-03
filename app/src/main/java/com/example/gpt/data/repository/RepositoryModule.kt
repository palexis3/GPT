package com.example.gpt.data.repository

import com.example.gpt.data.repository.chat.ChatRepository
import com.example.gpt.data.repository.chat.ChatRepositoryImpl
import com.example.gpt.data.repository.image.ImageRepository
import com.example.gpt.data.repository.image.ImageRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {
    @Binds
    @ViewModelScoped
    fun bindChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository

    @Binds
    @ViewModelScoped
    fun bindImageRepository(imageRepositoryImpl: ImageRepositoryImpl): ImageRepository
}
