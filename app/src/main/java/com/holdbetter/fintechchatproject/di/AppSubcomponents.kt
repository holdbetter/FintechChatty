package com.holdbetter.fintechchatproject.di

import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di.ChannelsSubcomponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.channels.di.StreamSubcomponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.di.NavigationSubcomponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di.DetailUserSubcomponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.people.di.PeopleSubcomponent
import com.holdbetter.fintechchatproject.app.bottomnavigation.navigation.profile.di.ProfileSubcomponent
import com.holdbetter.fintechchatproject.app.chat.di.ChatSubcomponent
import com.holdbetter.fintechchatproject.app.hoster.di.HostSubcomponent
import com.holdbetter.fintechchatproject.app.load.di.LoadingSubcomponent
import dagger.Module

@Module(
    subcomponents = [
        LoadingSubcomponent::class,
        HostSubcomponent::class,
        ChannelsSubcomponent::class,
        StreamSubcomponent::class,
        PeopleSubcomponent::class,
        NavigationSubcomponent::class,
        ChatSubcomponent::class,
        DetailUserSubcomponent::class,
        ProfileSubcomponent::class
    ]
)
class AppSubcomponents