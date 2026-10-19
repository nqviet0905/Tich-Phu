package com.example.personalfinance.data.model

import com.google.gson.annotations.SerializedName


/*
|--------------------------------------------------------------------------
| REQUEST THANH TOÁN QR
|--------------------------------------------------------------------------
*/

data class QrPaymentRequest(

    @SerializedName("bank_account_id")
    val bankAccountId: Long,

    @SerializedName("merchant_name")
    val merchantName: String,

    @SerializedName("merchant_bank_code")
    val merchantBankCode: String,

    @SerializedName("merchant_account_number")
    val merchantAccountNumber: String,

    @SerializedName("category_id")
    val categoryId: Long,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("note")
    val note: String? = null
)


/*
|--------------------------------------------------------------------------
| KẾT QUẢ THANH TOÁN
|--------------------------------------------------------------------------
*/

data class QrPaymentResult(

    @SerializedName("transaction_id")
    val transactionId: Long,

    @SerializedName("reference_code")
    val referenceCode: String,

    @SerializedName("merchant_name")
    val merchantName: String,

    @SerializedName("category_id")
    val categoryId: Long,

    @SerializedName("category_name")
    val categoryName: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("payment_method")
    val paymentMethod: String,

    @SerializedName("transaction_date")
    val transactionDate: String,

    @SerializedName("transaction_time")
    val transactionTime: String
)


/*
|--------------------------------------------------------------------------
| GỢI Ý DANH MỤC THEO CỬA HÀNG
|--------------------------------------------------------------------------
*/

data class MerchantCategorySuggestion(

    @SerializedName("merchant_name")
    val merchantName: String,

    @SerializedName("category_id")
    val categoryId: Long?,

    @SerializedName("category_name")
    val categoryName: String?
)