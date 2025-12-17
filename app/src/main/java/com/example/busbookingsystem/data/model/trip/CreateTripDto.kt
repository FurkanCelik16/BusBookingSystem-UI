package com.example.busbookingsystem.data.model.trip

data class CreateTripDto(
    val companyId: Int,
    val busId: Int,
    val originCityId: Int,
    val originDistrictId: Int?,
    val destinationCityId: Int,
    val destinationDistrictId: Int?,
    val departureDate: String,
    val departureTime: String,

    val price: Double
)