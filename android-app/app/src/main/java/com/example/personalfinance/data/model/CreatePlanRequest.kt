package com.example.personalfinance.data.model

data class CreatePlanRequest(
    val plan_name: String?,
    val month: Int,
    val year: Int,
    val monthly_income: Double,
    val fixed_expense: Double,
    val saving_target: Double
)