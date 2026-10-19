package com.example.personalfinance.data.model

data class PlanDetailData(
    val id: Long,
    val plan_name: String?,
    val monthly_income: Double,
    val fixed_expense: Double,
    val saving_target: Double,
    val flexible_budget: Double,
    val month: Int,
    val year: Int,
    val status: String,
    val allocations: List<PlanAllocation> = emptyList()
)

data class PlanAllocation(
    val id: Long,
    val category_id: Long,
    val category_name: String,
    val category_icon: String?,
    val planned_amount: Double,
    val note: String?
)