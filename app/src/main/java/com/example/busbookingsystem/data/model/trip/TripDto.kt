package com.example.busbookingsystem.data.model.trip

import com.example.busbookingsystem.data.model.ticket.TicketDto
import com.google.gson.annotations.SerializedName

data class TripDto(
    val id: Int,

    // Backend'de "OriginCityName" olarak geliyor
    @SerializedName("originCityName")
    val originCityName: String?,

    val originDistrictName: String? = null,

    // Backend'de "DestinationCityName" olarak geliyor
    @SerializedName("destinationCityName")
    val destinationCityName: String?,

    val destinationDistrictName:String? =null,

    @SerializedName("departureDate")
    val departureDate: String, // "2025-12-12"

    @SerializedName("departureTime")
    val departureTime: String?, // "14:30:00"

    val price: Double,

    // Otobüs/Firma Bilgisi
    @SerializedName("companyName")
    val companyName: String?,

    @SerializedName("soldTicketCount")
    val soldTicketCount: Int = 0,

    val busPlateNumber: String?,
    val tickets: List<TicketDto>? = null
)