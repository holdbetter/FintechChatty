package com.holdbetter.fintechchatproject.di

import com.holdbetter.fintechchatproject.domain.repository.IStreamRepository
import com.holdbetter.fintechchatproject.domain.repository.StreamRepository
import dagger.Binds
import dagger.Module

@Module
interface StreamModule {
    @Binds
    @ApplicationScope
    fun getStreamRepository(streamRepository: StreamRepository): IStreamRepository
}