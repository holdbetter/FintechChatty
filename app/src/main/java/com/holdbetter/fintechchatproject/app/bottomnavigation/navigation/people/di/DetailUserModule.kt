package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di

import com.holdbetter.fintechchatproject.di.FragmentScope
import com.holdbetter.fintechchatproject.domain.repository.IUserRepository
import com.holdbetter.fintechchatproject.domain.repository.UserRepository
import dagger.Binds
import dagger.Module

@Module
interface DetailUserModule {
    @Binds
    @FragmentScope
    fun getUserRepository(userRepository: UserRepository): IUserRepository
}