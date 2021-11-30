package com.holdbetter.fintechchatproject.app.hoster.di

import com.holdbetter.fintechchatproject.app.hoster.HostFragment
import dagger.Subcomponent

@Subcomponent
interface HostSubcomponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): HostSubcomponent
    }

    fun inject(hostFragment: HostFragment)
}