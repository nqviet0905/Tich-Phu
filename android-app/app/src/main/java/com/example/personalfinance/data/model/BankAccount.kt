    package com.example.personalfinance.data.model

    import com.google.gson.annotations.SerializedName

    data class BankAccount(

        @SerializedName("id")
        val id: Long,

        @SerializedName("bank_code")
        val bankCode: String,

        @SerializedName("bank_name")
        val bankName: String,

        @SerializedName("account_number")
        val accountNumber: String,

        @SerializedName("masked_account_number")
        val maskedAccountNumber: String? = null,

        @SerializedName("account_name")
        val accountName: String,

        @SerializedName("is_default")
        val isDefault: Boolean = false,

        @SerializedName("is_active")
        val isActive: Boolean = true,

        @SerializedName("created_at")
        val createdAt: String? = null
    )


    data class CreateBankAccountRequest(

        @SerializedName("bank_code")
        val bankCode: String,

        @SerializedName("bank_name")
        val bankName: String,

        @SerializedName("account_number")
        val accountNumber: String,

        @SerializedName("account_name")
        val accountName: String,

        @SerializedName("is_default")
        val isDefault: Boolean = false
    )


    data class BankAccountActionRequest(

        @SerializedName("bank_account_id")
        val bankAccountId: Long
    )


    data class BankAccountActionData(

        @SerializedName("bank_account_id")
        val bankAccountId: Long
    )