package com.example.busbookingsystem.utils

object TokenManager {
    private var token: String? = null

    fun saveToken(newToken: String) {
        token = newToken
    }

    fun getToken(): String? {
        return token
    }

    fun clearToken() {
        token = null
    }
}