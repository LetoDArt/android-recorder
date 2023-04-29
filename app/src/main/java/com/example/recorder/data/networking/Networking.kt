package com.example.recorder.data.networking

import com.example.recorder.data.auth.AuthApi
import com.example.recorder.data.auth.AuthRepository
import com.example.recorder.data.auth.TokenStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import timber.log.Timber

object Networking {

    private var okhttpClient: OkHttpClient? = null
    private var retrofit: Retrofit? = null

    val authApi: AuthApi
        get() = retrofit?.create() ?: error("retrofit is not initialized")

    fun init() {
        okhttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(
                HttpLoggingInterceptor {
                    Timber.tag("Network").d(it)
                }
                    .setLevel(HttpLoggingInterceptor.Level.BODY)
            )
            .addNetworkInterceptor(AuthInterceptor())
            .addInterceptor(AuthorizationFailedInterceptor(TokenStorage, AuthRepository()))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://192.168.31.194:8000")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okhttpClient!!)
            .build()
    }



}