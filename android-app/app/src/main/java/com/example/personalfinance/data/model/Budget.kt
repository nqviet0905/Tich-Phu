package com.example.personalfinance.data.model

data class Budget(
    val id: Long,
    val category_id: Long,
    val category_name: String,
    val amount: Double,
    val month: Int,
    val year: Int,
    val warning_level: Int,
    val spent_amount: Double,
    val remaining_amount: Double,
    val usage_percentage: Double,
    val status: String
)