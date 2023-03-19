package com.example.recorder.ui.Signup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recorder.R
import com.example.recorder.data.auth.AuthRepository
import com.example.recorder.data.auth.model.SignupRequest
import com.example.recorder.utils.isEmailValid
import com.example.recorder.utils.isPasswordValid
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SignupViewModel(application: Application): AndroidViewModel(application) {

    private val authRepository = AuthRepository()

    private val signupSuccess = Channel<Unit>(Channel.BUFFERED)
    private val toastEventChannel = Channel<Int>(Channel.BUFFERED)


    val toastFlow: Flow<Int>
        get() = toastEventChannel.receiveAsFlow()

    val signupFlow: Flow<Unit>
        get() = signupSuccess.receiveAsFlow()

    fun processRegData(
        email: String,
        username: String,
        firstName: String,
        lastName: String,
        password: String,
        repeatPassword: String,
    ) {
        if (!validateName(username) || !validateName(firstName) || !validateName(lastName)) return
        if (!validateEmail(email)) return
        if (!validatePasswords(password, repeatPassword)) return

        signup(SignupRequest(email, username, firstName, lastName, password))
    }

    fun signup(signupRequest: SignupRequest) {
        viewModelScope.launch {
            runCatching {
                authRepository.createNewUser(signupRequest)
            }.onSuccess {
                signupSuccess.trySendBlocking(Unit)
            }.onFailure {
                Timber.tag("Attempt").e(it)
            }
        }
    }

    private fun validateName(name: String): Boolean {
        if (name == "") {
            toastEventChannel.trySendBlocking(R.string.non_valid_name)
            return false
        }
        return true
    }

    private fun validateEmail(email: String): Boolean {
        if (!email.isEmailValid()) {
            toastEventChannel.trySendBlocking(R.string.non_valid_email)
            return false
        }
        return true
    }

    private fun validatePasswords(password: String, repPassword: String): Boolean {
        if (!password.isPasswordValid()) {
            toastEventChannel.trySendBlocking(R.string.non_valid_password)
            return false
        }
        if (password != repPassword) {
            toastEventChannel.trySendBlocking(R.string.password_do_not_coincide)
            return false
        }
        return true
    }

}