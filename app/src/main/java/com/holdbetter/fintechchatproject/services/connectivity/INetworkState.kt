package com.holdbetter.fintechchatproject.services.connectivity

import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities

interface INetworkState {
    val isNetworkConnected: Boolean
    val network: Network?
    val networkCapabilities: NetworkCapabilities?
    val linkProperties: LinkProperties?
}