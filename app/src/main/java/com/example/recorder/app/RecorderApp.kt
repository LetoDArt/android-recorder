package com.example.recorder.app

import timber.log.Timber
import android.app.Application
import com.example.recorder.data.networking.Networking

class RecorderApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Networking.init(this)
        Timber.plant(Timber.DebugTree())
    }
}
