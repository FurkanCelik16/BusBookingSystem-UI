package com.example.busbookingsystem.data.model.passenger

data class CreatePassengerDto(
    val firstName: String,
    val lastName: String,
    val tcNo: String,
    val email: String,
    val phoneNumber: String,
    val gender: Int,
    val dateOfBirth: String
)