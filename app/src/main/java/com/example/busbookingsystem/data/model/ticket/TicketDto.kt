package com.example.busbookingsystem.data.model.ticket

import com.example.busbookingsystem.data.model.trip.TripDto
import com.example.busbookingsystem.data.model.passenger.PassengerDto
import com.google.gson.annotations.SerializedName

data class TicketDto(
    val id: Int,
    val seatNumber: Int,
    val price: Double,
    val isPaid: Boolean,
    @SerializedName("trip")
    val trip: TripDto?,
    @SerializedName("passenger")
    val passenger: PassengerDto?,
    val isReserved: Boolean,
    val reservationExpiresAt: String?, // Tarih string olarak gelir
    val createdDate: String?,
)