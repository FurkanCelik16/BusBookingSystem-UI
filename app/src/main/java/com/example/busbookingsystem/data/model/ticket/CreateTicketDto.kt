package com.example.busbookingsystem.data.model.ticket

data class CreateTicketDto(
    val tripId: Int,
    val passengerId: Int,
    val seatNumber: Int,
    val paidAmount: Double
)