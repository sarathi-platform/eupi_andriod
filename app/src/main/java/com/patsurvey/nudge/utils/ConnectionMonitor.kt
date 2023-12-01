package com.patsurvey.nudge.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import com.patsurvey.nudge.MyApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory


class ConnectionMonitor(context: Context) : LiveData<NetworkInfo>() {
    val TAG = "ConnectionMonitor"

    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()
    private var downLoadSpeed: Int = 0

    private fun checkValidNetworks() {
        NudgeLogger.d(TAG, "checkValidNetworks : ${validNetworks.toString()}")
        if (validNetworks.isNotEmpty()) {
            validNetworks.forEach { network ->
                NudgeLogger.d(TAG, "checkValidNetworks : validNetworks.forEach -> $network")
                checkValidNetworkAvailability(network)
            }
        }
        //  postValue(validNetworks.size > 0)
        postValue(
            NetworkInfo(
                validNetworks.size > 0,
                downLoadSpeed,
                getNetworkSpeed(downLoadSpeed)
            )
        )
    }

    override fun onActive() {
        networkCallback = createNetworkCallback()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun createNetworkCallback() = object : ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            NudgeLogger.d(TAG, "onAvailable: $network")
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            NudgeLogger.d(TAG, "onAvailable: ${network}, $hasInternetCapability")

            if (hasInternetCapability == true) {
                // Check if this network actually has internet
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            NudgeLogger.d(TAG, "onAvailable: adding network. $network")
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
            downLoadSpeed = networkCapabilities?.linkDownstreamBandwidthKbps!!
            postValue(
                NetworkInfo(
                    validNetworks.size > 0,
                    downLoadSpeed,
                    getNetworkSpeed(downLoadSpeed)
                )
            )
            getNetworkSpeed(downLoadSpeed)
            val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
            NudgeLogger.d(TAG, "onCapabilitiesChanged: ${network}, $hasInternetCapability")

            if (hasInternetCapability == true) {
                // Check if this network actually has internet
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            NudgeLogger.d(TAG, "onAvailable: adding network. $network")
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
            NudgeLogger.d(TAG, "onLost: $network")
            NudgeLogger.d(TAG, "onLost: ${validNetworks.toString()} ")
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }

    fun checkValidNetworkAvailability(validNetwork: Network) {
        NudgeLogger.d(TAG, "checkValidNetworkAvailability: $validNetwork")
        val networkCapabilities = connectivityManager.getNetworkCapabilities(validNetwork)
        val hasInternetCapability = networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
        NudgeLogger.d(TAG, "checkValidNetworkAvailability: ${validNetwork}, $hasInternetCapability")

        if (hasInternetCapability == true) {
            // Check if this network actually has internet
            CoroutineScope(Dispatchers.IO).launch {
                val hasInternet = DoesNetworkHaveInternet.execute(validNetwork.socketFactory)
                if (hasInternet) {
                    withContext(Dispatchers.Main) {
                        NudgeLogger.d(TAG, "checkValidNetworkAvailability: adding network. $validNetwork")
                        validNetworks.add(validNetwork)
                        MyApplication.addValidNetworkToList(validNetwork)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        NudgeLogger.d(TAG, "checkValidNetworkAvailability: removing network. $validNetwork")
                        validNetworks.remove(validNetwork)
                        MyApplication.removeValidNetworkToList(validNetwork)
                    }
                }
            }
        } else {
            NudgeLogger.d(TAG, "checkValidNetworkAvailability: removing network. $validNetwork")
            validNetworks.remove(validNetwork)
            MyApplication.removeValidNetworkToList(validNetwork)
        }
    }

    fun getNetworkSpeed(downloadSpeed: Int): String {
        return if (downloadSpeed < 128) {
            NetworkSpeed.POOR.toString()
        } else if (downloadSpeed < 3600) {
            NetworkSpeed.MODERATE.toString()
        } else if (downloadSpeed < 23000) {
            NetworkSpeed.GOOD.toString()
        } else if (downloadSpeed > 23000) {
            NetworkSpeed.EXCELLENT.toString()
        } else {
            NetworkSpeed.UNKNOWN.toString()
        }
    }

    object DoesNetworkHaveInternet {
        const val TAG = "DoesNetworkHaveInternet"
        fun execute(socketFactory: SocketFactory): Boolean {
            // Make sure to execute this on a background thread.
            return try {
                NudgeLogger.d(TAG, "PINGING Google...")
                val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
                socket.connect(InetSocketAddress("8.8.8.8", 53), 1500)
                socket.close()
                NudgeLogger.d(TAG, "PING success.")
                true
            } catch (e: IOException) {
                NudgeLogger.e(TAG, "No Internet Connection. $e")
                false
            }
        }
    }
}

enum class NetworkSpeed {
    POOR, MODERATE, GOOD, EXCELLENT, UNKNOWN
}

