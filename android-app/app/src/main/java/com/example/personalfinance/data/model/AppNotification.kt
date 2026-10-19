package com.example.personalfinance.data.model

data class AppNotification(

    val id: Long,

    val title: String,

    val message: String,

    val type: String = "system",

    val reference_id: Long? = null,

    val is_read: Int = 0,

    val created_at: String? = null
)


