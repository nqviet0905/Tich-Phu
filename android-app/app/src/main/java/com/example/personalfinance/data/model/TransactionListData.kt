package com.example.personalfinance.data.model

data class TransactionListData(
    val items: List<Transaction> = emptyList(),
    val pagination: Pagination? = null
)

data class Pagination(
    val page: Int,
    val limit: Int,
    val total: Int,
    val total_pages: Int
)