package com.example.busbookingsystem.data.model.passenger

data class PassengerDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val tcNo: String,
    val email: String,
    val phoneNumber: String,
    val gender: Int,
    val dateOfBirth: String
)