package com.patsurvey.nudge.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import com.nudge.core.utils.CoreLogger
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.nio.ByteBuffer
import javax.inject.Inject

class ConnectionMonitorV2 @Inject constructor(@ApplicationContext private val context: Context) {


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

    fun getIpAddress(): String? {
        val network = connectivityManager.activeNetwork ?: return null
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return null

        try {
            when {
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                    val wifiManager =
                        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val ipAddress = wifiManager.connectionInfo.ipAddress
                    return InetAddress.getByAddress(
                        ByteBuffer.allocate(4).putInt(ipAddress).array().reversedArray()
                    ).hostAddress ?: null
                }

                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                    val en = NetworkInterface.getNetworkInterfaces()
                    while (en.hasMoreElements()) {
                        val networkInterface = en.nextElement()
                        val enumIpAddr = networkInterface.inetAddresses
                        while (enumIpAddr.hasMoreElements()) {
                            val inetAddress = enumIpAddr.nextElement()
                            if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                                return inetAddress.hostAddress ?: null
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            CoreLogger.e(
                tag = "ConnectionMonitorV2",
                msg = "getIpAddress: execption-> ${e.message}",
                ex = e,
                stackTrace = true
            )
            return null
        }

        return null
    }

}