package com.holdbetter.fintechchatproject.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.holdbetter.fintechchatproject.R
import com.holdbetter.fintechchatproject.main.viewmodel.EmojiViewModel
import com.holdbetter.fintechchatproject.main.viewmodel.EmojiViewModelFactory
import com.holdbetter.fintechchatproject.services.ContextExtensions.app
import com.holdbetter.fintechchatproject.services.connectivity.ChatNetworkCallback
import com.holdbetter.fintechchatproject.services.connectivity.NetworkState
import com.holdbetter.fintechchatproject.services.connectivity.NetworkStateHolder

class MainActivity : AppCompatActivity() {
    private lateinit var state: NetworkState
    private lateinit var callback: ChatNetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        state = NetworkState()
        callback = ChatNetworkCallback(state)
        NetworkStateHolder.bindState(state)
        app.connectivityManager.registerDefaultNetworkCallback(callback)
    }

    override fun onPause() {
        super.onPause()

        app.connectivityManager.unregisterNetworkCallback(callback)
    }
}