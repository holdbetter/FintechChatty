package com.holdbetter.fintechchatproject.app.hoster.di

import com.holdbetter.fintechchatproject.app.hoster.HostFragment
import com.holdbetter.fintechchatproject.di.RepositoryDependencies
import dagger.Component

@Component(dependencies = [RepositoryDependencies::class])
interface HostComponent {
    @Component.Factory
    interface Factory {
        fun create(repositoryDependencies: RepositoryDependencies): HostComponent
    }

    fun inject(hostFragment: HostFragment)
}