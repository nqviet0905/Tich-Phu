package com.example.personalfinance.data.model

data class CreateGoalRequest(
    val name: String,
    val description: String?,
    val target_amount: Double,
    val current_amount: Double,
    val start_date: String,
    val deadline: String
)