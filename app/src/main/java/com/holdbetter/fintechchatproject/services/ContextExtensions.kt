package com.holdbetter.fintechchatproject.services

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.TypedValue
import com.holdbetter.fintechchatproject.app.ChatApplication

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

    fun ConnectivityManager.checkConnectionStatusSynchronously(): Boolean {
        return allNetworks.mapNotNull { network -> getNetworkCapabilities(network) }
            .any { networksCapability ->
                networksCapability.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networksCapability.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networksCapability.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            }
    }

    val Context.app
        get() = when (this) {
            is Activity -> (application as ChatApplication)
            else -> (applicationContext as ChatApplication)
        }
}