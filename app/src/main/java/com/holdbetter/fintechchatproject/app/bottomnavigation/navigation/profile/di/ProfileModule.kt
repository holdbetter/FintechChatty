package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.di

import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.domain.repository.PersonalRepository
import dagger.Binds
import dagger.Module

@Module
interface ProfileModule {
    @Binds
    fun getProfileRepository(personalRepository: PersonalRepository): IPersonalRepository
}