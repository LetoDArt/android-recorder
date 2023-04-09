package com.example.recorder.ui.MainWindow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recorder.R
import com.example.recorder.data.auth.AuthRepository
import com.example.recorder.data.auth.TokenStorage
import com.example.recorder.data.auth.model.User
import com.example.recorder.data.common.UserStore
import com.example.recorder.data.websocket.SocketListener
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import timber.log.Timber

class MainWindowViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository = AuthRepository()

    private val user = MutableStateFlow<User?>(null)
    private val navigate = Channel<Int>(Channel.BUFFERED)


    val userFlow: Flow<User?>
        get() = user.asStateFlow()

    val navigateFlow: Flow<Int>
        get() = navigate.receiveAsFlow()

    fun getUserFromStore() {
        user.value = UserStore.currentUser
    }

    fun obtainUser(): String {
        return UserStore.currentUser?.user_name!!
    }

    fun logout() {
        viewModelScope.launch {
            runCatching {
                authRepository.logout()
            }.onSuccess {
                navigate.trySendBlocking(R.id.MainToLogin)
            }.onFailure {
                Timber.tag("Attempt").e(it)
            }
        }
    }

    fun connectSocket(viewModel: SocketViewModel): WebSocket {
        val client = OkHttpClient()
        val user = this.obtainUser()
        val token = TokenStorage.accessToken

        val req: Request = Request
            .Builder()
            .url("ws://192.168.31.237:8000/ws/stream/${user}/?token=${token}")
            .build()
        val listener = SocketListener(viewModel)

        return client.newWebSocket(req, listener)
    }


}