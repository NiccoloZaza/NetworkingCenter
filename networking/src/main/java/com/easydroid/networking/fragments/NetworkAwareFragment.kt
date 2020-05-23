@file:Suppress("unused")

package com.easydroid.networking.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.easydroid.networking.NetworkCenter
import com.easydroid.networking.enums.Connectivity
import com.easydroid.networking.enums.ConnectivityStrength
import com.easydroid.networking.enums.ConnectivityType
import com.easydroid.networking.listeners.IOnConnectivityChangeListener

abstract class NetworkAwareFragment: Fragment(), IOnConnectivityChangeListener {
    //region IOnConnectivityChangeListener implementation
    override fun connectivityChanged(
        connectivity: Connectivity,
        connectivityStrength: ConnectivityStrength,
        connectivityType: ConnectivityType
    ) {}
    //endregion

    //region Fields
    protected val networkState: Connectivity
        get() = NetworkCenter.instance.connectionState

    protected val networkAvailable: Boolean
        get() = NetworkCenter.instance.isConnected
    //endregion

    //region Overridden Methods
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val declaringClass = this::class.java.getMethod(
            "onConnectivityChanged",
            Connectivity::class.java,
            ConnectivityStrength::class.java,
            ConnectivityType::class.java
        ).declaringClass
        val shouldMonitorNetwork: Boolean =
            declaringClass.name.compareTo(NetworkAwareFragment::class.qualifiedName.toString()) != 0
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