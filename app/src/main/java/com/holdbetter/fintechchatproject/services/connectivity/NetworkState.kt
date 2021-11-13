package com.holdbetter.fintechchatproject.services.connectivity

import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities

class NetworkState : INetworkState {
    override var isNetworkConnected: Boolean = false
    override var network: Network? = null
    override var networkCapabilities: NetworkCapabilities? = null
    override var linkProperties: LinkProperties? = null
}