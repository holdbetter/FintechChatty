package com.holdbetter.fintechchatproject.di

import com.holdbetter.fintechchatproject.room.ChatDatabase
import com.holdbetter.fintechchatproject.room.dao.EmojiDao
import com.holdbetter.fintechchatproject.room.dao.PeopleDao
import com.holdbetter.fintechchatproject.room.dao.PersonalDao
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import dagger.Module
import dagger.Provides

@Module
class DaoModule {
    @Provides
    fun getEmojiDao(chatDatabase: ChatDatabase): EmojiDao = chatDatabase.emojiDao()

    @Provides
    fun getPeopleDao(chatDatabase: ChatDatabase): PeopleDao = chatDatabase.peopleDao()

    @Provides
    fun getStreamDao(chatDatabase: ChatDatabase): StreamDao = chatDatabase.streamDao()

    @Provides
    fun getPersonalDao(chatDatabase: ChatDatabase): PersonalDao = chatDatabase.personalDao()
}