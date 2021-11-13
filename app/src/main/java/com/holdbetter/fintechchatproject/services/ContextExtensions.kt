package com.holdbetter.fintechchatproject.services

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.TypedValue
import androidx.core.content.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.holdbetter.fintechchatproject.domain.exception.NotConnectedException
import com.holdbetter.fintechchatproject.domain.retrofit.ServiceProvider
import com.holdbetter.fintechchatproject.domain.retrofit.TinkoffZulipApi
import com.holdbetter.fintechchatproject.main.ChatApplication
import com.holdbetter.fintechchatproject.services.connectivity.NetworkStateHolder
import io.reactivex.rxjava3.core.Single

object ContextExtensions {
    fun Context.dpToPx(dp: Float): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            this.resources.displayMetrics
        )
        return px.toInt()
    }

    fun Context.spToPx(sp: Float): Int {
        val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp,
            this.resources.displayMetrics
        )
        return px.toInt()
    }

    fun Application.checkConnectionStatusSynchronously(): Boolean {
        val connectivityManager = getSystemService<ConnectivityManager>()
        connectivityManager?.let { cm ->
            return cm.allNetworks.mapNotNull { network -> cm.getNetworkCapabilities(network) }
                .any { networksCapability ->
                    networksCapability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networksCapability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networksCapability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                }
        }
        return false
    }

    fun AndroidViewModel.isConnected(): Single<Boolean> {
        return Single.just(
            if (NetworkStateHolder.network != null) {
                NetworkStateHolder.isNetworkConnected
            } else {
                getApplication<ChatApplication>().checkConnectionStatusSynchronously()
            }
        )
    }
}