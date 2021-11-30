package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.PeopleFragment
import dagger.Subcomponent

@Subcomponent(modules = [PeopleModule::class])
interface PeopleSubcomponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): PeopleSubcomponent
    }

    fun inject(peopleFragment: PeopleFragment)
}