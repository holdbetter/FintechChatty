package com.holdbetter.fintechchatproject.di

import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.domain.repository.PersonalRepository
import dagger.Binds
import dagger.Module

@Module
interface ProfileModule {
    @Binds
    fun getPersonalRepository(personalRepository: PersonalRepository): IPersonalRepository
}