package com.example.busbookingsystem.data.model.auth

import com.example.busbookingsystem.data.model.auth.UserData
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("body")    val data: UserData?
)