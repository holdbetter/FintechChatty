package com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.ChannelsFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view.AllStreamsFragment
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.view.SubbedStreamsFragment
import com.holdbetter.fintechchatproject.di.AndroidDependencies
import com.holdbetter.fintechchatproject.di.DomainDependencies
import com.holdbetter.fintechchatproject.di.FragmentScope
import com.holdbetter.fintechchatproject.di.RepositoryDependencies
import dagger.Component

@FragmentScope
@Component(
    modules = [ChannelsModule::class],
    dependencies = [AndroidDependencies::class, DomainDependencies::class, RepositoryDependencies::class]
)
interface ChannelsComponent {
    @Component.Factory
    interface Factory {
        fun create(
            androidDependencies: AndroidDependencies,
            domainDependencies: DomainDependencies,
            repositoryDependencies: RepositoryDependencies
        ): ChannelsComponent
    }

    fun inject(channelsFragment: ChannelsFragment)
    fun inject(allStreamsFragment: AllStreamsFragment)
    fun inject(subbedStreamsFragment: SubbedStreamsFragment)
}