package com.bizilabs.streeek.lib.remote.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class NetworkInterceptor(
    val context: Context,
) : Interceptor {
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun intercept(chain: Interceptor.Chain): Response {
        if (isNetworkConnectionOn().not()) throw IOException("Network connection is not on. Please turn on to continue.")

        if (isNetworkConnectionAvailable().not()) throw IOException("Network Connection has no internet.")

        return chain.proceed(chain.request())
    }

    /**
     * Checks if network connection is on
     */
    private fun isNetworkConnectionOn(): Boolean {
        val network = manager.activeNetwork
        val connection = manager.getNetworkCapabilities(network)
        return connection != null &&
            connection.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET,
            )
    }

    /**
     * Checks if network connection is available
     */
    private fun isNetworkConnectionAvailable(): Boolean {
        val activeNetwork = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }
    }
}
