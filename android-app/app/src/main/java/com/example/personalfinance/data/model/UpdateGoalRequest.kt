package com.example.personalfinance.data.model

data class UpdateGoalRequest(
    val id: Long,
    val name: String,
    val description: String?,
    val target_amount: Double,
    val start_date: String,
    val deadline: String,
    val status: String
)