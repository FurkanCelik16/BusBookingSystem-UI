package com.example.busbookingsystem.data.model.location

import com.example.busbookingsystem.data.model.location.DistrictDto

data class CityDto(
    val id: Int,
    val name: String,
    val districts: List<DistrictDto>?
)