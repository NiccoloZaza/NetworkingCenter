package com.easydroid.networking.observers

import com.easydroid.networking.enums.Connectivity
import com.easydroid.networking.enums.ConnectivityStrength
import com.easydroid.networking.enums.ConnectivityType
import com.easydroid.networking.listeners.IOnConnectivityChangeListener
import java.util.*

internal class NetworkParams(
    val connectivityState: Connectivity,
    val connectivityStrength: ConnectivityStrength,
    val connectivityType: ConnectivityType
)

class NetworkObserver(
    val observer: IOnConnectivityChangeListener
) : Observer {
    override fun update(p0: Observable?, p1: Any?) {
        p1 as NetworkParams
        observer.connectivityChanged(
            p1.connectivityState,
            p1.connectivityStrength,
            p1.connectivityType
        )
    }
}