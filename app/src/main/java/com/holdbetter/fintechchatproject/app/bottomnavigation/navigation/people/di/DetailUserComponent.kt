package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.view.DetailUserContent
import com.holdbetter.fintechchatproject.di.AndroidDependencies
import com.holdbetter.fintechchatproject.di.DomainDependencies
import com.holdbetter.fintechchatproject.di.FragmentScope
import com.holdbetter.fintechchatproject.di.RepositoryDependencies
import dagger.Component

@FragmentScope
@Component(dependencies = [AndroidDependencies::class, DomainDependencies::class, RepositoryDependencies::class])
interface DetailUserComponent {
    @Component.Factory
    interface Factory {
        fun create(
            androidDependencies: AndroidDependencies,
            domainDependencies: DomainDependencies,
            repositoryDependencies: RepositoryDependencies
        ): DetailUserComponent
    }

    fun inject(detailUserContent: DetailUserContent)
}