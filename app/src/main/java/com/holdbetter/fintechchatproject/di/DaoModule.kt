package com.holdbetter.fintechchatproject.di

import com.holdbetter.fintechchatproject.room.ChatDatabase
import com.holdbetter.fintechchatproject.room.dao.EmojiDao
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import dagger.Module
import dagger.Provides

@Module
class DaoModule {
    @Provides
    fun getEmojiDao(chatDatabase: ChatDatabase): EmojiDao = chatDatabase.emojiDao()

    @Provides
    fun getStreamDao(chatDatabase: ChatDatabase): StreamDao = chatDatabase.streamDao()
}