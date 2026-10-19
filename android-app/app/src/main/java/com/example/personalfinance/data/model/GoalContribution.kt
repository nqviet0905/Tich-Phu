package com.example.personalfinance.data.model

data class GoalContribution(
    val id: Long,
    val goal_id: Long,
    val amount: Double,
    val contribution_date: String,
    val note: String?,
    val created_at: String?
)