package com.example.personalfinance.data.model

data class Transaction(

    val id: Long,

    val category_id: Long,

    val category_name: String,

    val amount: Double,

    val type: String,

    val transaction_date: String,

    val transaction_time: String? = null,

    val merchant_name: String? = null,

    val payment_method: String? = null,

    val bank_account_id: Long? = null,

    val reference_code: String? = null,

    // ==============================
    // BANK INFO
    // ==============================

    val bank_code: String? = null,

    val bank_name: String? = null,

    val bank_account_name: String? = null,

    val masked_account_number: String? = null,

    val note: String?,

    val created_at: String?,

    val updated_at: String?
)