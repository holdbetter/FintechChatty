package com.holdbetter.fintechchatproject.services.connectivity

import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities

object NetworkStateHolder : INetworkState {
    private var state: INetworkState? = null

    override val isNetworkConnected: Boolean
        get() = state?.isNetworkConnected ?: false
    override val network: Network?
        get() = state?.network
    override val networkCapabilities: NetworkCapabilities?
        get() = state?.networkCapabilities
    override val linkProperties: LinkProperties?
        get() = state?.linkProperties

    fun bindState(networkState: INetworkState) {
        state = networkState
    }
}