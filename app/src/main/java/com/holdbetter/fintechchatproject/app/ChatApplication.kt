package com.holdbetter.fintechchatproject.app

import android.app.Application
import com.holdbetter.fintechchatproject.di.DaggerAppComponent

class ChatApplication : Application() {
    val appComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}