package com.easydroid.sample.app

import android.app.Application
import com.easydroid.networking.NetworkCenter

class SampleApp: Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkCenter.init(this)
    }
}