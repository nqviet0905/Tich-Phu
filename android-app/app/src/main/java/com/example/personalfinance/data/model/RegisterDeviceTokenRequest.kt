package com.example.personalfinance.data.model

data class RegisterDeviceTokenRequest(
    val token: String,
    val platform: String = "android"
)