package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di

import com.holdbetter.fintechchatproject.di.FragmentScope
import com.holdbetter.fintechchatproject.domain.repository.IPeopleRepository
import com.holdbetter.fintechchatproject.domain.repository.PeopleRepository
import dagger.Binds
import dagger.Module

@Module
interface PeopleModule {
    @Binds
    @FragmentScope
    fun getPeopleRepository(peopleRepository: PeopleRepository): IPeopleRepository
}