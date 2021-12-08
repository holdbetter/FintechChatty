package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.ProfileFragment
import dagger.Subcomponent

@Subcomponent
interface ProfileSubcomponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ProfileSubcomponent
    }

    fun inject(profileFragment: ProfileFragment)
}