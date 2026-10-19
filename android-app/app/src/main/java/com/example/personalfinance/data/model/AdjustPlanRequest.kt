package com.example.personalfinance.data.model

data class AdjustPlanRequest(
    val plan_id: Long,
    val allocations: List<PlanAllocationRequest>
)

data class PlanAllocationRequest(
    val category_id: Long,
    val planned_amount: Double,
    val note: String? = null
)