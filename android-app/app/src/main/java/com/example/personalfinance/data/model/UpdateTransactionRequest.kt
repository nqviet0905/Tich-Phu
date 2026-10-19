package com.example.personalfinance.data.model

data class UpdateTransactionRequest(
    val id: Long,
    val category_id: Long,
    val type: String,
    val amount: Double,
    val transaction_date: String,
    val note: String?
)