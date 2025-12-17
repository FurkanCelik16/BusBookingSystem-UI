package com.example.busbookingsystem.data.model.bus

data class CreateBusDto(
    val plateNumber: String,
    val totalSeatCount: Int,
    val companyId: Int,
    val brand:String
)