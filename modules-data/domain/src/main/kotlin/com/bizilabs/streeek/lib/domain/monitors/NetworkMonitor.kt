package com.bizilabs.streeek.lib.domain.monitors

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class NetworkMonitor(
    private val context: Context,
    private val repository: PreferenceRepository,
) : ConnectivityManager.NetworkCallback() {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val request =
        NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

    init {
        checkNetworkState()
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        Timber.d("Netty -> Has network connection : true")
        scope.launch { repository.updateNetworkConnection(hasNetworkConnection = true) }
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        Timber.d("Netty -> Has network connection : false")
        scope.launch { repository.updateNetworkConnection(hasNetworkConnection = false) }
    }

    private fun isNetworkAvailable(): Boolean {
        Timber.d("Netty -> checking if connected")
        val manager = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
            else -> false
        }.also {
            Timber.d("Netty -> Has network connection : $it")
        }
    }

    private fun checkNetworkState() {
        Timber.d("Netty -> checking network availability")
        val isConnected = isNetworkAvailable()
        scope.launch { repository.updateNetworkConnection(hasNetworkConnection = isConnected) }
    }

    fun register() {
        manager.registerNetworkCallback(request, this)
    }

    fun unregister() {
        manager.unregisterNetworkCallback(this)
    }
}
