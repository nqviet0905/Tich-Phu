package com.example.personalfinance.data.model

data class CreateBudgetRequest(
    val category_id: Long,
    val amount: Double,
    val month: Int,
    val year: Int,
    val warning_level: Int
)