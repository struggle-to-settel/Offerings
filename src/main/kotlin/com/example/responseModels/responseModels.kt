package com.example.responseModels


data class User(
    var _id: String? = null,
    val username: String?,
    val password: String?,
    val fullName: String?,
    var token: String?,
    val phoneNumber: String?,
    val emailAddress: String?,
    val location: GeoLocation?
)

data class GeoLocation(
    val latitude: Double?,
    val longitute: Double?
)

data class Login(
    val username: String, val password: String
)