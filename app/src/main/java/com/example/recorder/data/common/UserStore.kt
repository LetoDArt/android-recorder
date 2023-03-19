package com.example.recorder.data.common

import com.example.recorder.data.auth.model.User

object UserStore {
    private var user: User? = null

    val currentUser get() = this.getUser()

    fun saveUser(newUser: User?) {
        user = newUser
    }

    private fun getUser(): User? {
        return user
    }
}