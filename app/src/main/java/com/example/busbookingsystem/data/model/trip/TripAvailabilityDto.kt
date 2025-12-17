package com.example.busbookingsystem.data.model.trip

import com.example.busbookingsystem.data.model.trip.SeatAvailabilityDto
import com.google.gson.annotations.SerializedName

data class TripAvailabilityDto(
    @SerializedName("tripId")        val tripId: Int,
    @SerializedName("totalSeats")    val totalSeats: Int,
    @SerializedName("availableSeats") val availableSeats: Int,
    @SerializedName("occupiedSeats") val occupiedSeats: Int,
    @SerializedName("seats")         val seats: List<SeatAvailabilityDto>
) {
}