package com.nrlm.baselinesurvey.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

class ConnectionMonitor(context: Context) : LiveData<Boolean>() {
    val TAG = "ConnectionMonitor"

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    private fun checkValidNetworks() {
        BaselineLogger.d(TAG, "checkValidNetworks : ${validNetworks.toString()}")
        if (validNetworks.isNotEmpty()) {
            validNetworks.forEach { network ->
                BaselineLogger.d(TAG, "checkValidNetworks : validNetworks.forEach -> $network")
                checkValidNetworkAvailability(network)
            }
        }
        postValue(validNetworks.size > 0)
    }

    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            BaselineLogger.d(TAG, "onAvailable: $network")
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            BaselineLogger.d(TAG, "onAvailable: ${network}, $hasInternetCapability")

            if (hasInternetCapability == true) {
                // Check if this network actually has internet
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            BaselineLogger.d(TAG, "onAvailable: adding network. $network")
                            validNetworks.add(network)
                            checkValidNetworks()
                        }
                    }
                }
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            BaselineLogger.d(TAG, "onCapabilitiesChanged: ${network}, $hasInternetCapability")

            if (hasInternetCapability == true) {
                // Check if this network actually has internet
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            BaselineLogger.d(TAG, "onAvailable: adding network. $network")
                            validNetworks.add(network)
                            checkValidNetworks()
                        }
                    } else {
                        checkValidNetworks()
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            BaselineLogger.d(TAG, "onLost: $network")
            BaselineLogger.d(TAG, "onLost: ${validNetworks.toString()} ")
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    fun checkValidNetworkAvailability(validNetwork: Network) {
        BaselineLogger.d(TAG, "checkValidNetworkAvailability: $validNetwork")
        val networkCapabilities = connectivityManager.getNetworkCapabilities(validNetwork)
        val hasInternetCapability = networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        BaselineLogger.d(TAG, "checkValidNetworkAvailability: ${validNetwork}, $hasInternetCapability")

        if (hasInternetCapability == true) {
            // Check if this network actually has internet
            CoroutineScope(Dispatchers.IO).launch {
                val hasInternet = DoesNetworkHaveInternet.execute(validNetwork.socketFactory)
                if (hasInternet) {
                    withContext(Dispatchers.Main) {
                        BaselineLogger.d(TAG, "checkValidNetworkAvailability: adding network. $validNetwork")
                        validNetworks.add(validNetwork)
                        BaselineCore.addValidNetworkToList(validNetwork)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        BaselineLogger.d(TAG, "checkValidNetworkAvailability: removing network. $validNetwork")
                        validNetworks.remove(validNetwork)
                        BaselineCore.removeValidNetworkToList(validNetwork)
                    }
                }
            }
        } else {
            BaselineLogger.d(TAG, "checkValidNetworkAvailability: removing network. $validNetwork")
            validNetworks.remove(validNetwork)
            BaselineCore.removeValidNetworkToList(validNetwork)
        }
    }

    object DoesNetworkHaveInternet {
        const val TAG="DoesNetworkHaveInternet"
        fun execute(socketFactory: SocketFactory): Boolean {
            // Make sure to execute this on a background thread.
            return try {
                BaselineLogger.d(TAG, "PINGING Google...")
                val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                socket.close()
                BaselineLogger.d(TAG, "PING success.")
                true
            } catch (e: IOException) {
                BaselineLogger.e(TAG, "No Internet Connection. $e")
                false
            }
        }
    }
}
