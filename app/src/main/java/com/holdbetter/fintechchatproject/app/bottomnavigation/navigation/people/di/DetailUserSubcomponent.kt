package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.ProfileContent
import dagger.Subcomponent

@Subcomponent(modules = [PeopleModule::class])
interface DetailUserSubcomponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): DetailUserSubcomponent
    }

    fun inject(profileContent: ProfileContent)
}