package com.easydroid.networking.listeners

import com.easydroid.networking.enums.Connectivity
import com.easydroid.networking.enums.ConnectivityStrength
import com.easydroid.networking.enums.ConnectivityType

interface IOnConnectivityChangeListener {
    fun connectivityChanged(connectivity: Connectivity, connectivityStrength: ConnectivityStrength, connectivityType: ConnectivityType)
}