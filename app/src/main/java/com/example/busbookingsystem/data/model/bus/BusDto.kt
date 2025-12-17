package com.example.busbookingsystem.data.model.bus

import com.google.gson.annotations.SerializedName

data class BusDto(
    val id: Int,
    @SerializedName("plateNumber")
    val plateNumber: String,
    val totalSeatCount: Int,
    val companyId: Int,
    @SerializedName("companyName")
    val companyName: String?
)