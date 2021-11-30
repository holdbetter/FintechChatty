package com.holdbetter.fintechchatproject.app.chat.di

import com.holdbetter.fintechchatproject.domain.repository.ChatRepository
import com.holdbetter.fintechchatproject.domain.repository.IChatRepository
import dagger.Binds
import dagger.Module

@Module
interface ChatModule {
    @Binds
    fun getChatRepository(chatRepository: ChatRepository): IChatRepository
}