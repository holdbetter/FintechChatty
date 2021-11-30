package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.ChannelsFragment
import dagger.Subcomponent

@Subcomponent
interface ChannelsSubcomponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): ChannelsSubcomponent
    }

    fun inject(channelsFragment: ChannelsFragment)
}