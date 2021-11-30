package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.NavigationFragment
import dagger.Subcomponent

@Subcomponent(modules = [ProfileModule::class])
interface NavigationSubcomponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): NavigationSubcomponent
    }

    fun inject(navigationFragment: NavigationFragment)
}