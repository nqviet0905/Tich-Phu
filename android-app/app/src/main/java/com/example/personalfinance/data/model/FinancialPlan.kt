package com.example.personalfinance.data.model

data class FinancialPlan(
    val id: Long,
    val plan_name: String?,
    val monthly_income: Double,
    val fixed_expense: Double,
    val saving_target: Double,
    val flexible_budget: Double,
    val month: Int,
    val year: Int,
    val status: String,

    val allocation_count: Int = 0,

    val created_at: String?,
    val updated_at: String?
)