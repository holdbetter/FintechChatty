package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view.AllStreamsFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view.SubbedStreamsFragment
import dagger.Subcomponent

@Subcomponent
interface StreamSubcomponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): StreamSubcomponent
    }

    fun inject(allStreamsFragment: AllStreamsFragment)
    fun inject(subbedStreamsFragment: SubbedStreamsFragment)
}