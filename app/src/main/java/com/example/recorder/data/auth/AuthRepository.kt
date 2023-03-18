package com.example.recorder.data.auth

import com.example.recorder.data.auth.model.*
import com.example.recorder.data.networking.Networking

class AuthRepository {
    fun logout() {
        TokenStorage.saveAccessToken("")
        TokenStorage.saveRefreshToken("")
    }

    suspend fun createNewUser(signupRequest: SignupRequest): SignupResponse  {
        return Networking.authApi.createNewUser(signupRequest)
    }

    suspend fun loginUser(loginRequest: LoginRequest): User  {
        val tokens = Networking.authApi.loginUser(loginRequest)

        TokenStorage.saveAccessToken(tokens.access)
        TokenStorage.saveRefreshToken(tokens.refresh)

        return Networking.authApi.getUser()
    }

    suspend fun getUser(): User {
        return Networking.authApi.getUser()
    }

    suspend fun refreshTokens(refreshRequest: RefreshRequest): LoginResponse {
        return Networking.authApi.refreshTokens(refreshRequest)
    }
}