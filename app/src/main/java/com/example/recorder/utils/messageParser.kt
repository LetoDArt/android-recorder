package com.example.recorder.utils

fun String.convertToMessage(): String {
    return "{ \"message\": \"${this}\" }"
}