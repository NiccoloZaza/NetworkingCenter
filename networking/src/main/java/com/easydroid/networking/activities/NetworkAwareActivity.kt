@file:Suppress("unused")

package com.easydroid.networking.activities

import android.app.Activity
import android.os.Bundle
import com.easydroid.networking.NetworkCenter
import com.easydroid.networking.enums.Connectivity
import com.easydroid.networking.enums.ConnectivityStrength
import com.easydroid.networking.enums.ConnectivityType
import com.easydroid.networking.listeners.IOnConnectivityChangeListener

abstract class NetworkAwareActivity: Activity(), IOnConnectivityChangeListener {
    //region IOnConnectivityChangeListener implementation
    override fun connectivityChanged(
        connectivity: Connectivity,
        connectivityStrength: ConnectivityStrength,
        connectivityType: ConnectivityType
    ) {}
    //endregion

    //region Overridden Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val declaringClass = this::class.java.getMethod(
            "onConnectivityChanged",
            Connectivity::class.java,
            ConnectivityStrength::class.java,
            ConnectivityType::class.java
        ).declaringClass
        val shouldMonitorNetwork: Boolean =
            declaringClass.name.compareTo(NetworkAwareActivity::class.qualifiedName.toString()) != 0
        if (shouldMonitorNetwork) {
            NetworkCenter.instance.addObserver(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        NetworkCenter.instance.removeObserver(this)
    }
    //endregion
}