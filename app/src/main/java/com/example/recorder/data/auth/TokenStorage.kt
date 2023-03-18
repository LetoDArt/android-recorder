package com.example.recorder.data.auth

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

object TokenStorage {
    private var pref: SharedPreferences? = null

    val accessToken get() = this.obtain("access_token")
    val refreshToken get() = this.obtain("refresh_token")

    fun init(context: Context) {
        pref = context.getSharedPreferences("tokens_store", MODE_PRIVATE)
    }

    private fun save(key: String, value: String) {
        val editor = pref?.edit()!!
        editor.putString(key, value)
        editor.apply()
    }

    private fun obtain(key: String): String {
        return pref?.getString(key, "")!!
    }

    fun saveAccessToken(access: String) {
        this.save("access_token", access)
    }

    fun saveRefreshToken(refresh: String) {
        this.save("refresh_token", refresh)
    }
}