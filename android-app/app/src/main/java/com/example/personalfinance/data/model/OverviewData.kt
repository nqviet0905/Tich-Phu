package com.example.personalfinance.data.model

data class OverviewData(

    val month: Int,

    val year: Int,

    val total_income: Double,

    val total_expense: Double,

    val balance: Double,

    val saving_rate: Double,

    val transaction_count: Int,

    val total_budget: Double,

    val budget_usage_percentage: Double,

    val active_goals: Int,

    val goal_name: String? = null,

    val goal_current_amount: Double = 0.0,

    val goal_target_amount: Double = 0.0,

    val goal_progress: Double = 0.0,

    val unread_notification_count: Int = 0
)