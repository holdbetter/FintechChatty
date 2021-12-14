package com.holdbetter.fintechchatproject.di

import com.holdbetter.fintechchatproject.domain.repository.*
import dagger.Binds
import dagger.Module

@Module
interface RepositoryModule {
    @Binds
    @ApplicationScope
    fun getEmojiRepository(emojiRepository: EmojiRepository): IEmojiRepository

    @Binds
    @ApplicationScope
    fun getPersonalRepository(personalRepository: PersonalRepository): IPersonalRepository

    @Binds
    fun getChatRepository(chatRepository: ChatRepository): IChatRepository
}