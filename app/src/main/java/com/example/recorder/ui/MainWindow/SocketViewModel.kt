package com.example.recorder.ui.MainWindow

import android.app.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class SocketViewModel(application: Application): AndroidViewModel(application) {
    private val liveData = MutableStateFlow<String?>(null)
    val messageFlow: Flow<String?> get() = liveData.asStateFlow()

    fun outputData(string: String) {
        liveData.value = string
    }
}