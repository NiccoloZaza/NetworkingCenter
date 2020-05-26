package com.easydroid.sample

import android.os.Bundle
import android.widget.Toast
import com.easydroid.networking.NetworkCenter
import com.easydroid.networking.activities.NetworkAwareCompatActivity
import com.easydroid.networking.enums.Connectivity
import com.easydroid.networking.enums.ConnectivityStrength
import com.easydroid.networking.enums.ConnectivityType
import kotlinx.android.synthetic.main.activity_main.*

class CustomAwareActivity : NetworkAwareCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showInfo()
    }

    override fun connectivityChanged(
        connectivity: Connectivity,
        connectivityStrength: ConnectivityStrength,
        connectivityType: ConnectivityType
    ) {
        showInfo()

        Toast.makeText(
            this,
            networkInfo,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showInfo() {
        network_status_text.text = networkInfo
    }

    private val networkInfo: String
        get() {
            return if (networkAvailable)
                "Network Available(${NetworkCenter.instance.connectionType.name})"
            else
                "Network Not Available"
        }
}
