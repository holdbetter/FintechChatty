package com.holdbetter.fintechchatproject.di

import android.app.Application
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat
import com.holdbetter.fintechchatproject.room.ChatDatabase
import com.holdbetter.fintechchatproject.services.connectivity.INetworkState
import com.holdbetter.fintechchatproject.services.connectivity.NetworkState
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [AppModule.Network::class])
class AppModule {
    @ApplicationScope
    @Provides
    fun getDatabase(app: Application): ChatDatabase {
        return ChatDatabase.getDatabase(app)
    }

    @ApplicationScope
    @Provides
    fun getConnectivityManager(app: Application): ConnectivityManager = ContextCompat.getSystemService(
        app,
        ConnectivityManager::class.java
    )!!

    @Module
    interface Network {
        @Binds
        fun getNetworkState(networkState: NetworkState): INetworkState
    }
}