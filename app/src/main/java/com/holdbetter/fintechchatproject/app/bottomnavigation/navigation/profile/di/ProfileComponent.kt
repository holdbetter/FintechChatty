package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.ProfileFragment
import com.holdbetter.fintechchatproject.di.AndroidDependencies
import com.holdbetter.fintechchatproject.di.DomainDependencies
import com.holdbetter.fintechchatproject.di.FragmentScope
import com.holdbetter.fintechchatproject.di.RepositoryDependencies
import dagger.Component

@FragmentScope
@Component(dependencies = [AndroidDependencies::class, DomainDependencies::class, RepositoryDependencies::class])
interface ProfileComponent {
    @Component.Factory
    interface Factory {
        fun create(
            androidDependencies: AndroidDependencies,
            domainDependencies: DomainDependencies,
            repositoryDependencies: RepositoryDependencies
        ): ProfileComponent
    }

    fun inject(profileFragment: ProfileFragment)
}