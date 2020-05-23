package com.easydroid.networking.observables

import com.easydroid.networking.enums.Connectivity
import com.easydroid.networking.enums.ConnectivityStrength
import com.easydroid.networking.enums.ConnectivityType
import com.easydroid.networking.observers.NetworkParams
import java.util.*

class NetworkObservable : Observable() {
    override fun hasChanged(): Boolean {
        return true
    }

    fun connectivityChanged(connectivity: Connectivity, connectivityStrength: ConnectivityStrength, connectivityType: ConnectivityType) {
        notifyObservers(NetworkParams(connectivity, connectivityStrength, connectivityType))
    }
}