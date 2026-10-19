package com.example.personalfinance.data.model

data class BudgetListData(
    val budgets: List<Budget> = emptyList(),
    val summary: BudgetSummary? = null
)

data class BudgetSummary(
    val total_budget: Double = 0.0,
    val total_spent: Double = 0.0,
    val total_remaining: Double = 0.0
)