package com.easydroid.networking.extensions

import com.easydroid.networking.NetworkCenter
import com.easydroid.networking.enums.Connectivity

val Any.networkState: Connectivity
    get() = NetworkCenter.instance.connectionState

val Any.networkAvailable: Boolean
    get() = NetworkCenter.instance.isConnected