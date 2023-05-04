package com.patsurvey.nudge.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData

const val TAG = "ConnectionMonitor"

class ConnectionMonitor(context: Context) : LiveData<Boolean>() {

    private val connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    private var connectionStatus = false

    private fun updateNetworkStatus() {
        postValue(connectionStatus)
    }

    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

    }

    private fun createNetworkCallback()= object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null) {
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            connectionStatus = true
                            updateNetworkStatus()
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            connectionStatus = true
                            updateNetworkStatus()
                        }
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            connectionStatus = true
                            updateNetworkStatus()
                        }
                    }
                } else {
                    connectionStatus = false
                    updateNetworkStatus()
                }
            } else {
                val activeNetworkInfo = connectivityManager.activeNetworkInfo
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                    connectionStatus = true
                    updateNetworkStatus()
                } else {
                    connectionStatus = false
                    updateNetworkStatus()
                }
            }
        }

        override fun onLost(network: Network) {
            Log.d(TAG, "onLost: Network Lost")
            connectionStatus = false
            updateNetworkStatus()
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    Log.d(TAG, "networkCapabilities: TRANSPORT_CELLULAR")
                    connectionStatus = true
                    updateNetworkStatus()
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    Log.d(TAG, "networkCapabilities: TRANSPORT_WIFI")
                    connectionStatus = true
                    updateNetworkStatus()
                }
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                    Log.d(TAG, "networkCapabilities: TRANSPORT_ETHERNET")
                    connectionStatus = true
                    updateNetworkStatus()
                }
                else -> {
                    Log.d(TAG, "networkCapabilities: Network Lost")
                    connectionStatus = false
                    updateNetworkStatus()
                }
            }
        }
    }

    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

}
