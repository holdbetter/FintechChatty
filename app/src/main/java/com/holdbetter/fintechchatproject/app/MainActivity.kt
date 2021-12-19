package com.holdbetter.fintechchatproject.app

import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.services.ContextExtensions.app
import com.holdbetter.fintechchatproject.services.connectivity.ChatNetworkCallback
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var networkCallback: ChatNetworkCallback

    @Inject
    lateinit var connectivityManager: ConnectivityManager

    override fun onCreate(savedInstanceState: Bundle?) {
        app.appComponent.inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onPause() {
        super.onPause()

        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun addNetworkCallback(callback: (Boolean) -> Unit) {
        networkCallback.addCallback(callback)
    }

    fun removeNetworkCallback(callback: (Boolean) -> Unit) {
        networkCallback.removeCallback(callback)
    }

    val isNetworkAvailable: Boolean
        get() = networkCallback.isAvailable()
}