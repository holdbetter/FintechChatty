package com.holdbetter.fintechchatproject.di

import com.holdbetter.fintechchatproject.domain.repository.EmojiRepository
import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import dagger.Binds
import dagger.Module

@Module
interface EmojiModule {
    @Binds
    fun getEmojiRepository(emojiRepository: EmojiRepository): IEmojiRepository
}