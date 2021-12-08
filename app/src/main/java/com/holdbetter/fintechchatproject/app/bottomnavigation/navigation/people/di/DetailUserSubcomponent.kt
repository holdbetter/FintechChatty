package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.DetailUserContent
import dagger.Subcomponent

@Subcomponent(modules = [PeopleModule::class])
interface DetailUserSubcomponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): DetailUserSubcomponent
    }

    fun inject(detailUserContent: DetailUserContent)
}