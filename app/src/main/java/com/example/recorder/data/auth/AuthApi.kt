package com.example.recorder.data.auth

import com.example.recorder.data.auth.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/login")
    suspend fun loginUser(@Body loginRequest: LoginRequest): LoginResponse

    @POST("/api/auth/logout")
    suspend fun logoutUser(@Body logoutRequest: LogoutRequest): MessageResponse

    @POST("/api/auth/signup")
    suspend fun createNewUser(@Body signupRequest: SignupRequest): SignupResponse

    @POST("/api/auth/token/refresh")
    suspend fun refreshTokens(@Body refreshRequest: RefreshRequest): LoginResponse

    @GET("/api/auth/user/current")
    suspend fun getUser(): User
}