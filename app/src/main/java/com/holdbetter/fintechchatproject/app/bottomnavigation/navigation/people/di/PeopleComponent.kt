package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.PeopleFragment
import com.holdbetter.fintechchatproject.di.AndroidDependencies
import com.holdbetter.fintechchatproject.di.DomainDependencies
import com.holdbetter.fintechchatproject.di.FragmentScope
import com.holdbetter.fintechchatproject.di.RepositoryDependencies
import com.holdbetter.fintechchatproject.domain.repository.IPeopleRepository
import dagger.Component

@FragmentScope
@Component(
    modules = [PeopleModule::class],
    dependencies = [AndroidDependencies::class, DomainDependencies::class, RepositoryDependencies::class]
)
interface PeopleComponent : PeopleDependencies {
    override fun getPeopleRepository(): IPeopleRepository

    @Component.Factory
    interface Factory {
        fun create(
            androidDependencies: AndroidDependencies,
            domainDependencies: DomainDependencies,
            repositoryDependencies: RepositoryDependencies
        ): PeopleComponent
    }

    fun inject(peopleFragment: PeopleFragment)
}

interface PeopleDependencies {
    fun getPeopleRepository(): IPeopleRepository
}