package com.holdbetter.fintechchatproject.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.services.ContextExtensions.app
import com.holdbetter.fintechchatproject.services.connectivity.ChatNetworkCallback
import com.holdbetter.fintechchatproject.services.connectivity.NetworkState
import com.holdbetter.fintechchatproject.services.connectivity.NetworkStateHolder

class MainActivity : AppCompatActivity() {
    private lateinit var networkState: NetworkState
    private lateinit var networkCallback: ChatNetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        networkState = NetworkState()
        networkCallback = ChatNetworkCallback(networkState)
        NetworkStateHolder.bindState(networkState)
        app.connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    override fun onPause() {
        super.onPause()

        app.connectivityManager.unregisterNetworkCallback(networkCallback)
    }
}