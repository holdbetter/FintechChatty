package com.holdbetter.fintechchatproject.di

import android.app.Application
import com.holdbetter.fintechchatproject.app.MainActivity
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di.ChannelsSubcomponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di.StreamSubcomponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di.DetailUserSubcomponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di.PeopleSubcomponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.di.NavigationSubcomponent
import com.holdbetter.fintechchatproject.app.chat.di.ChatSubcomponent
import com.holdbetter.fintechchatproject.app.hoster.di.HostSubcomponent
import com.holdbetter.fintechchatproject.app.load.di.LoadingSubcomponent
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        AppModule::class,
        AppSubcomponents::class,
        DaoModule::class,
        EmojiModule::class,
        StreamModule::class,
        DomainModule::class
    ]
)
abstract class AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }

    abstract fun inject(mainActivity: MainActivity)

    abstract fun loadingComponent(): LoadingSubcomponent.Factory
    abstract fun hostComponent(): HostSubcomponent.Factory
    abstract fun channelsComponent(): ChannelsSubcomponent.Factory
    abstract fun streamComponent(): StreamSubcomponent.Factory
    abstract fun peopleComponent(): PeopleSubcomponent.Factory
    abstract fun navigationComponent(): NavigationSubcomponent.Factory
    abstract fun chatComponent(): ChatSubcomponent.Factory
    abstract fun detailUserComponent(): DetailUserSubcomponent.Factory
}