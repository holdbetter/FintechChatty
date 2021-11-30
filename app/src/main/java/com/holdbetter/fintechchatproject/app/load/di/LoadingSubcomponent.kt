package com.holdbetter.fintechchatproject.app.load.di

import com.holdbetter.fintechchatproject.app.load.LoadingFragment
import dagger.Subcomponent

@Subcomponent
interface LoadingSubcomponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): LoadingSubcomponent
    }

    fun inject(loadingFragment: LoadingFragment)
}