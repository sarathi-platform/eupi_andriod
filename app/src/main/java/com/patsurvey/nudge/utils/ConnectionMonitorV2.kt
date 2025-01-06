package com.patsurvey.nudge.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class ConnectionMonitorV2 @Inject constructor(@ApplicationContext context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isConnected = MutableStateFlow(false)
    val isConnected: Flow<Boolean> = _isConnected
    private val _validNetworks: MutableSet<Network> = HashSet<Network>()

    private val validNetworks: Set<Network> get() = _validNetworks

    init {
        // Observe network connectivity changes
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: android.net.Network) {
                    _validNetworks.add(network)
                    _isConnected.value = validNetworks.isNotEmpty()
                }


                override fun onLost(network: android.net.Network) {
                    _validNetworks.remove(network)

                    _isConnected.value = validNetworks.isNotEmpty()
                }
            })
    }

}