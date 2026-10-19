package com.example.personalfinance.data.model

data class CategoryStatisticData(
    val month: Int,
    val year: Int,
    val type: String,
    val total_amount: Double,
    val categories: List<CategoryStatistic>
)

data class CategoryStatistic(
    val category_id: Long,
    val category_name: String,
    val category_icon: String?,
    val transaction_count: Int,
    val total_amount: Double,
    val percentage: Double
)