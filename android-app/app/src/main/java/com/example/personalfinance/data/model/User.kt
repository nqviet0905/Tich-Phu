package com.example.personalfinance.data.model

data class User(
    val id: Long,
    val firebase_uid: String?,
    val name: String?,
    val email: String?,
    val avatar_url: String?,
    val currency: String?
)