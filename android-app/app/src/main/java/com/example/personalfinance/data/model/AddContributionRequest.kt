package com.example.personalfinance.data.model

data class AddContributionRequest(
    val goal_id: Long,
    val amount: Double,
    val contribution_date: String,
    val note: String?
)