package com.example.personalfinance.data.model

data class UpdateBudgetRequest(
    val id: Long,
    val amount: Double,
    val warning_level: Int
)