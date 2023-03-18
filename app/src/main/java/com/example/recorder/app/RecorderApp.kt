package com.example.recorder.app

import android.app.Application
import com.example.recorder.data.auth.TokenStorage
import com.example.recorder.data.networking.Networking
import timber.log.Timber

class RecorderApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Networking.init()
        TokenStorage.init(this)
        Timber.plant(Timber.DebugTree())
    }
}
