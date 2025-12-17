package com.example.busbookingsystem.data.model.auth

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("token")     val token: String,
    @SerializedName("email")     val email: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName")  val lastName: String?,
    @SerializedName("role")      val role: String
)