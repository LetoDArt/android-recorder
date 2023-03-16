package com.example.recorder.data.auth

import com.example.recorder.data.auth.model.LoginRequest
import com.example.recorder.data.auth.model.SignupRequest
import com.example.recorder.data.auth.model.SignupResponse
import com.example.recorder.data.auth.model.User
import com.example.recorder.data.networking.Networking
import timber.log.Timber

class AuthRepository {
    fun corruptAccessToken() {
        TokenStorage.accessToken = "fake token"
    }

    fun logout() {
        TokenStorage.accessToken = null
        TokenStorage.refreshToken = null
    }

    suspend fun createNewUser(signupRequest: SignupRequest): SignupResponse  {
        return Networking.authApi.createNewUser(signupRequest)
    }

    suspend fun loginUser(loginRequest: LoginRequest)  {
        val tokens = Networking.authApi.loginUser(loginRequest)
        Timber.tag("Attempt").d(tokens.access)
        TokenStorage.accessToken = tokens.access
        TokenStorage.refreshToken = tokens.refresh
    }

    suspend fun getUser(): User {
        return Networking.authApi.getUser()
    }
}