package com.holdbetter.fintechchatproject.di

import android.app.Application
import com.holdbetter.fintechchatproject.app.MainActivity
import com.holdbetter.fintechchatproject.app.chat.di.ChatSubcomponent
import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.domain.repository.IPeopleRepository
import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.room.ChatDatabase
import com.holdbetter.fintechchatproject.room.dao.PeopleDao
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        AppModule::class,
        AppSubcomponents::class,
        DaoModule::class,
        DomainModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent : AndroidDependencies, DomainDependencies, RepositoryDependencies {
    override fun getEmojiRepository(): IEmojiRepository

    override fun getPersonalRepository(): IPersonalRepository

    override fun getPeopleRepository(): IPeopleRepository

    override fun getConnectivityManager(): MyConnectivityManager

    override fun getDatabase(): ChatDatabase

    override fun getApi(): TinkoffZulipApi

    override fun getStreamDao(): StreamDao

    override fun getPeopleDao(): PeopleDao

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }

    fun inject(mainActivity: MainActivity)

    fun chatComponent(): ChatSubcomponent.Factory
}

interface RepositoryDependencies {
    fun getEmojiRepository(): IEmojiRepository
    fun getPersonalRepository(): IPersonalRepository
    fun getPeopleRepository(): IPeopleRepository
}

interface AndroidDependencies {
    fun getConnectivityManager(): MyConnectivityManager
    fun getDatabase(): ChatDatabase
    fun getStreamDao(): StreamDao
    fun getPeopleDao(): PeopleDao
}

interface DomainDependencies {
    fun getApi(): TinkoffZulipApi
}
