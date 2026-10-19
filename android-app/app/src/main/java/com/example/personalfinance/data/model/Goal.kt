package com.example.personalfinance.data.model

data class Goal(
    val id: Long,
    val name: String,
    val description: String?,
    val target_amount: Double,
    val current_amount: Double,
    val remaining_amount: Double,
    val progress_percentage: Double,
    val months_remaining: Int,
    val required_per_month: Double,
    val start_date: String,
    val deadline: String,
    val status: String,
    val created_at: String?,
    val updated_at: String?
)