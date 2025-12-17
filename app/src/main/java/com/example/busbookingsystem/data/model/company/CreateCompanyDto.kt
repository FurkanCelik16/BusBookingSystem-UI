package com.example.busbookingsystem.data.model.company

data class CreateCompanyDto(
    val name: String,
    val phone: String?,
    val email: String?,
    val address: String?
)