@file:Suppress("unused", "DEPRECATION")

package com.easydroid.networking

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import com.easydroid.networking.enums.Connectivity
import com.easydroid.networking.enums.ConnectivityStrength
import com.easydroid.networking.enums.ConnectivityType
import com.easydroid.networking.listeners.IOnConnectivityChangeListener
import com.easydroid.networking.observables.NetworkObservable
import com.easydroid.networking.observers.NetworkObserver
import com.easydroid.networking.receivers.NetworkBroadcastReceiver
import java.lang.IllegalStateException
import java.lang.reflect.Method

class NetworkCenter private constructor(private val appContext: Context) {
    companion object {
        private var obj: NetworkCenter? = null

        val instance: NetworkCenter
            get() {
                if (obj == null)
                    throw IllegalStateException("Network Center Has Not Been Initialized")
                else
                    return obj!!
            }

        @Synchronized
        fun init(applicationContext: Context) {
            if (obj != null)
                throw IllegalStateException("Network Center Has Already Been Initialized")
            else
                obj = NetworkCenter(applicationContext)
        }
    }

    //region Fields
    private var skippedFirstCall: Boolean = false

    private val networkObservable: NetworkObservable = NetworkObservable()

    private val intentFilter: IntentFilter = IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")

    private val networkReceiver: NetworkBroadcastReceiver = NetworkBroadcastReceiver()

    private val anonymousObservers: ArrayList<NetworkObserver> = arrayListOf()
    //endregion

    //region Properties
    val isConnected: Boolean get() = connectionState == Connectivity.Connected

    val connectionState: Connectivity
        get() {
            val connectivityManager =
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val capabilities =
                    connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                if (capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                )
                    Connectivity.Connected
                else
                    Connectivity.NotConnected
            } else if (connectivityManager.activeNetworkInfo?.isConnected == true)
                Connectivity.Connected
            else
                Connectivity.NotConnected
        }

    val connectionType: ConnectivityType
        get() {
            val connectivityManager =
                appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork
                val capabilities = connectivityManager.getNetworkCapabilities(network)
                if (capabilities != null)
                    when {
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectivityType.Wifi
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectivityType.Cellular
                        else -> ConnectivityType.None
                    }
                else
                    ConnectivityType.None
            } else {
                when (connectivityManager.activeNetworkInfo?.type) {
                    ConnectivityManager.TYPE_WIFI -> ConnectivityType.Wifi
                    ConnectivityManager.TYPE_MOBILE -> ConnectivityType.Cellular
                    else -> ConnectivityType.None
                }
            }
        }

    val connectionStrength: ConnectivityStrength
        get() {
            when (connectionType) {
                ConnectivityType.Cellular -> {
                    return when (cellularStrength) {
                        0 -> ConnectivityStrength.None
                        1 -> ConnectivityStrength.Poor
                        2 -> ConnectivityStrength.Normal
                        3 -> ConnectivityStrength.Excellent
                        else -> ConnectivityStrength.Unknown
                    }
                }
                ConnectivityType.Wifi -> {
                    val wifiManager: WifiManager =
                        appContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val wifiInfo: WifiInfo = wifiManager.connectionInfo
                    return when (WifiManager.calculateSignalLevel(wifiInfo.rssi, 5)) {
                        0 -> ConnectivityStrength.None
                        1, 2 -> ConnectivityStrength.Poor
                        3, 4 -> ConnectivityStrength.Normal
                        5 -> ConnectivityStrength.Excellent
                        else -> ConnectivityStrength.Unknown
                    }
                }
                else -> return ConnectivityStrength.Unknown
            }
        }

    private val cellularStrength: Int
        get() {
            try {
                return cellularStrengthReflect
            } catch (exception: Exception) {
                when ((appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkType) {
                    16,
                    TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_1xRTT,
                    TelephonyManager.NETWORK_TYPE_IDEN,
                    TelephonyManager.NETWORK_TYPE_CDMA -> return 1

                    17,
                    TelephonyManager.NETWORK_TYPE_UMTS,
                    TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_HSDPA,
                    TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_HSPA,
                    TelephonyManager.NETWORK_TYPE_EVDO_B,
                    TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_HSPAP -> return 2

                    18,
                    TelephonyManager.NETWORK_TYPE_LTE -> return 3

                    else -> return 0
                }
            }
        }

    private val cellularStrengthReflect: Int
        get() {
            val method: Method =
                TelephonyManager::class.java.getDeclaredMethod("getNetworkClass", Int::class.java)
            val accessible = method.isAccessible
            method.isAccessible = true
            val strength: Int = method.invoke(
                null,
                (appContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager).networkType
            ) as Int
            method.isAccessible = accessible
            return strength
        }
    //endregion

    //region Methods
    fun addObserver(observer: IOnConnectivityChangeListener) {
        val anonymousObserver = NetworkObserver(observer)
        anonymousObservers.add(anonymousObserver)
        addObserver(anonymousObserver)
    }

    fun removeObserver(observer: IOnConnectivityChangeListener) {
        val anonymousObserver = anonymousObservers.firstOrNull {
            it.observer == observer
        } ?: return
        anonymousObservers.remove(anonymousObserver)
        removeObserver(anonymousObserver)
    }

    fun addObserver(networkObserver: NetworkObserver) {
        networkObservable.addObserver(networkObserver)
        if (networkObservable.countObservers() == 1) {
            appContext.registerReceiver(networkReceiver, intentFilter)
        }
    }

    fun removeObserver(networkObserver: NetworkObserver) {
        networkObservable.deleteObserver(networkObserver)
        if (networkObservable.countObservers() == 0) {
            appContext.unregisterReceiver(networkReceiver)
        }
    }

    internal fun connectivityChanged() {
        if (!skippedFirstCall) {
            skippedFirstCall = true
            return
        }
        networkObservable.connectivityChanged(connectionState, connectionStrength, connectionType)
    }
    //endregion
}