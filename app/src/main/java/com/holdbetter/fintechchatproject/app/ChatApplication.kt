package com.holdbetter.fintechchatproject.app

import android.app.Application
import android.net.ConnectivityManager
import androidx.core.content.getSystemService
import com.holdbetter.fintechchatproject.app.di.PocketDI
import com.holdbetter.fintechchatproject.domain.repository.*
import com.holdbetter.fintechchatproject.room.ChatDatabase

class ChatApplication : Application() {
    val database by lazy { ChatDatabase.getDatabase(this) }
    val connectivityManager by lazy { getSystemService<ConnectivityManager>()!! }

    override fun onCreate() {
        super.onCreate()
        PocketDI.init(this)
    }
}