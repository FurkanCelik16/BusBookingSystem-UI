package com.example.busbookingsystem.data.model.trip

import com.google.gson.annotations.SerializedName

data class SeatAvailabilityDto(
    @SerializedName("seatNumber")    val seatNumber: Int,
    @SerializedName("isAvailable")   val isAvailable: Boolean,
    @SerializedName("status")        val status: String,
    @SerializedName("passengerName") val passengerName: String?,
    @SerializedName("reservationExpiresAt") val reservationExpiresAt: String?,
    val gender: Int = 0
)