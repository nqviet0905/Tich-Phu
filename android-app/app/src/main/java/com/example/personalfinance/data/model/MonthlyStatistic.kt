package com.example.personalfinance.data.model

data class MonthlyStatisticData(
    val year: Int,
    val summary: YearSummary,
    val months: List<MonthlyStatistic>
)

data class YearSummary(
    val total_income: Double,
    val total_expense: Double,
    val balance: Double,
    val saving_rate: Double
)

data class MonthlyStatistic(
    val month: Int,
    val total_income: Double,
    val total_expense: Double,
    val balance: Double,
    val saving_rate: Double
)