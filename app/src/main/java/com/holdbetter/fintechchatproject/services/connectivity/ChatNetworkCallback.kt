package com.holdbetter.fintechchatproject.services.connectivity

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import javax.inject.Inject

class ChatNetworkCallback @Inject constructor(private val networkState: NetworkState): ConnectivityManager.NetworkCallback()  {
    override fun onAvailable(network: Network) {
        networkState.network = network
        networkState.isNetworkConnected = true

        Log.d("NETWORK", "available")
    }

    override fun onLost(network: Network) {
        networkState.network = network
        networkState.isNetworkConnected = false

        Log.d("NETWORK", "lost")
    }

    override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
        networkState.networkCapabilities = networkCapabilities

        Log.d("NETWORK", "onCapabilitiesChanged")
    }

    override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
        networkState.linkProperties = linkProperties

        Log.d("NETWORK", "onLinkPropertiesChanged")
    }
}