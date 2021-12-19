package com.holdbetter.fintechchatproject.services.connectivity

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import javax.inject.Inject

class ChatNetworkCallback @Inject constructor(private val networkState: NetworkState): ConnectivityManager.NetworkCallback()  {
    private val callbacks: MutableList<(Boolean) -> Unit> = mutableListOf()

    override fun onAvailable(network: Network) {
        networkState.network = network
        networkState.isNetworkConnected = true

        for (callback in callbacks) {
            callback(true)
        }
    }

    override fun onLost(network: Network) {
        networkState.network = network
        networkState.isNetworkConnected = false

        for (callback in callbacks) {
            callback(false)
        }
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        networkState.networkCapabilities = networkCapabilities
    }

    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
        networkState.linkProperties = linkProperties
    }

    fun addCallback(callback: (Boolean) -> Unit) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: (Boolean) -> Unit) {
        callbacks.remove(callback)
    }

    fun isAvailable(): Boolean {
        return networkState.isNetworkConnected
    }
}