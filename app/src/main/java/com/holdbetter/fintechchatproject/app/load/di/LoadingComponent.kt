package com.holdbetter.fintechchatproject.app.load.di

import com.holdbetter.fintechchatproject.app.load.LoadingFragment
import com.holdbetter.fintechchatproject.di.RepositoryDependencies
import dagger.Component

@Component(dependencies = [RepositoryDependencies::class])
interface LoadingComponent {
    @Component.Factory
    interface Factory {
        fun create(
            repositoryDependencies: RepositoryDependencies
        ): LoadingComponent
    }

    fun inject(loadingFragment: LoadingFragment)
}