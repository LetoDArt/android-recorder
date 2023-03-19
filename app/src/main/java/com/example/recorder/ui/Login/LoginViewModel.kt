package com.example.recorder.ui.Login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recorder.R
import com.example.recorder.data.auth.AuthRepository
import com.example.recorder.data.auth.model.LoginRequest
import com.example.recorder.data.common.UserStore
import com.example.recorder.utils.isEmailValid
import com.example.recorder.utils.isPasswordValid
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginViewModel(application: Application): AndroidViewModel(application) {
    private val authRepository = AuthRepository()

    private val loginSuccess = Channel<Unit>(Channel.BUFFERED)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)

    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val signupFlow: Flow<Unit>
        get() = loginSuccess.receiveAsFlow()

    fun loginProcess(email: String, password: String) {
        if (!validateEmail(email) || !validatePasswords(password)) return
        login(LoginRequest(email, password))
    }


    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            runCatching {
                authRepository.loginUser(loginRequest)
            }.onSuccess {
                UserStore.saveUser(it)
                loginSuccess.trySendBlocking(Unit)
            }.onFailure {
                Timber.tag("Attempt").e(it)
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        if (!email.isEmailValid()) {
            toastEventChannel.trySendBlocking(R.string.non_valid_email)
            return false
        }
        return true
    }

    private fun validatePasswords(password: String): Boolean {
        if (!password.isPasswordValid()) {
            toastEventChannel.trySendBlocking(R.string.non_valid_password)
            return false
        }
        return true
    }
}