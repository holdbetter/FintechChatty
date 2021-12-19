package com.holdbetter.fintechchatproject.services.connectivity

import android.net.ConnectivityManager
import com.holdbetter.fintechchatproject.services.ContextExtensions.checkConnectionStatusSynchronously
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class MyConnectivityManager @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val networkState: INetworkState
) {
    val isConnected: Single<Boolean>
        get() {
            return Single.create {
                if (networkState.network != null) {
                    it.onSuccess(networkState.isNetworkConnected)
                } else {
                    it.onSuccess(connectivityManager.checkConnectionStatusSynchronously())
                }
            }
        }
}