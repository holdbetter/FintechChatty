package com.holdbetter.fintechchatproject.di

import android.app.Application
import com.holdbetter.fintechchatproject.app.MainActivity
import com.holdbetter.fintechchatproject.app.chat.view.EmojiBottomModalFragment
import com.holdbetter.fintechchatproject.domain.repository.IEmojiRepository
import com.holdbetter.fintechchatproject.domain.repository.IPersonalRepository
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.room.ChatDatabase
import com.holdbetter.fintechchatproject.room.dao.MessageDao
import com.holdbetter.fintechchatproject.room.dao.PeopleDao
import com.holdbetter.fintechchatproject.room.dao.StreamDao
import com.holdbetter.fintechchatproject.services.connectivity.MyConnectivityManager
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(
    modules = [
        AppModule::class,
        DaoModule::class,
        DomainModule::class,
        RepositoryModule::class
    ]
)
interface AppComponent : AndroidDependencies, DomainDependencies, RepositoryDependencies {
    override fun getEmojiRepository(): IEmojiRepository

    override fun getPersonalRepository(): IPersonalRepository

    override fun getConnectivityManager(): MyConnectivityManager

    override fun getDatabase(): ChatDatabase

    override fun getApi(): TinkoffZulipApi

    override fun getStreamDao(): StreamDao

    override fun getPeopleDao(): PeopleDao

    override fun getMessageDao(): MessageDao

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance app: Application): AppComponent
    }

    fun inject(mainActivity: MainActivity)
    fun inject(emojiBottomModalFragment: EmojiBottomModalFragment)
}

interface RepositoryDependencies {
    fun getEmojiRepository(): IEmojiRepository
    fun getPersonalRepository(): IPersonalRepository
}

interface AndroidDependencies {
    fun getConnectivityManager(): MyConnectivityManager
    fun getDatabase(): ChatDatabase
    fun getStreamDao(): StreamDao
    fun getPeopleDao(): PeopleDao
    fun getMessageDao(): MessageDao
}

interface DomainDependencies {
    fun getApi(): TinkoffZulipApi
}
