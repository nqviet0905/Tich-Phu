package com.example.personalfinance.data.model

data class Category(
    val id: Long,
    val name: String,
    val type: String,
    val icon: String?,
    val is_default: Int?,
    val is_active: Int?
)