package com.example.personalfinance.data.model

/*
|--------------------------------------------------------------------------
| NẠP TIỀN
|--------------------------------------------------------------------------
*/

data class DepositRequest(
    val amount: Double,
    val method: String,
    val note: String? = null
)

data class DepositData(
    val transaction_id: Long,
    val amount: Double,
    val balance: Double
)


/*
|--------------------------------------------------------------------------
| CHUYỂN TIỀN
|--------------------------------------------------------------------------
|
| receiver từ giờ là SỐ TÀI KHOẢN TÍCH PHÚ,
| không còn là email.
|
*/

data class TransferRequest(
    val receiver: String,
    val amount: Double,
    val note: String? = null
)

data class TransferData(
    val transfer_id: Long,
    val amount: Double,
    val balance: Double,
    val receiver_name: String?,
    val receiver_account_number: String?
)


/*
|--------------------------------------------------------------------------
| SỐ DƯ / VÍ
|--------------------------------------------------------------------------
*/

data class WalletBalanceData(
    val balance: Double,
    val account_number: String?
)


/*
|--------------------------------------------------------------------------
| NGƯỜI NHẬN
|--------------------------------------------------------------------------
|
| Dùng cho API recipient.php
| để tra cứu người nhận theo số tài khoản.
|
*/

data class RecipientData(
    val id: Long,
    val name: String?,
    val account_number: String
)