package com.example.recorder.data.auth.model

data class SignupRequest(
    val email: String,
    val user_name: String,
    val first_name: String,
    val last_name: String,
    val password: String,
)
