package com.example.busbookingsystem.data.model.auth

data class RegisterRequest(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)