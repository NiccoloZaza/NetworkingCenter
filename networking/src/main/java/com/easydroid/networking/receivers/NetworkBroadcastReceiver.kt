package com.easydroid.networking.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.easydroid.networking.NetworkCenter

internal class NetworkBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        NetworkCenter.instance.connectivityChanged()
    }
}