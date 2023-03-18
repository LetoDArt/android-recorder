package com.example.recorder.ui.Loader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recorder.R
import com.example.recorder.data.auth.AuthRepository
import com.example.recorder.data.common.UserStore
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class LoaderViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository = AuthRepository()

    private val redirectEventChannel = Channel<Int>(Channel.BUFFERED)

    val redirectFlow: Flow<Int>
        get() = redirectEventChannel.receiveAsFlow()


    fun getUser() {
        viewModelScope.launch {
            runCatching {
                authRepository.getUser()
            }.onSuccess {
                UserStore.saveUser(it)
                redirectEventChannel.trySendBlocking(R.id.StartingToMain)
            }.onFailure {
                Timber.tag("Attempt").e(it)
                redirectEventChannel.trySendBlocking(R.id.StartingToLogin)
            }
        }
    }


}