package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di

import com.holdbetter.fintechchatproject.di.FragmentScope
import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import com.holdbetter.fintechchatproject.domain.repository.StreamRepository
import com.holdbetter.fintechchatproject.room.ChatDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface ChannelsModule {
    @FragmentScope
    @Binds
    fun getStreamRepository(streamRepository: StreamRepository): IStreamRepository
}