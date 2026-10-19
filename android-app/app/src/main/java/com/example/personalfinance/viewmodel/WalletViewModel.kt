package com.example.personalfinance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.DepositData
import com.example.personalfinance.data.model.RecipientData
import com.example.personalfinance.data.model.TransferData
import com.example.personalfinance.data.repository.WalletRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/*
|--------------------------------------------------------------------------
| WALLET UI STATE
|--------------------------------------------------------------------------
*/

data class WalletUiState(

    val balance: Double = 0.0,

    val accountNumber: String? = null,

    val isLoadingBalance: Boolean = false,

    val isLookingUpRecipient: Boolean = false,

    val isSubmitting: Boolean = false,

    val error: String? = null,

    val successMessage: String? = null

) {

    /*
    |--------------------------------------------------------------------------
    | GIỮ TƯƠNG THÍCH VỚI UI CŨ
    |--------------------------------------------------------------------------
    */

    val isLoading: Boolean
        get() =
            isLoadingBalance ||
                    isLookingUpRecipient ||
                    isSubmitting
}


class WalletViewModel : ViewModel() {

    private val repository =
        WalletRepository()


    /*
    |--------------------------------------------------------------------------
    | UI STATE
    |--------------------------------------------------------------------------
    */

    private val _uiState =
        MutableStateFlow(
            WalletUiState()
        )

    val uiState: StateFlow<WalletUiState> =
        _uiState.asStateFlow()


    /*
    |--------------------------------------------------------------------------
    | RECIPIENT
    |--------------------------------------------------------------------------
    */

    private val _recipient =
        MutableStateFlow<RecipientData?>(
            null
        )

    val recipient: StateFlow<RecipientData?> =
        _recipient.asStateFlow()


    /*
    |--------------------------------------------------------------------------
    | LOOKUP JOB
    |--------------------------------------------------------------------------
    |
    | Nếu người dùng đang tra cứu số tài khoản A nhưng sửa sang
    | số B thì hủy request cũ để tránh dữ liệu người nhận bị lệch.
    |
    */

    private var lookupJob: Job? =
        null


    /*
    |--------------------------------------------------------------------------
    | LOAD BALANCE
    |--------------------------------------------------------------------------
    */

    fun loadBalance() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoadingBalance = true,
                    error = null
                )


            val result =
                repository.getBalance()


            result.onSuccess { data ->

                _uiState.value =
                    _uiState.value.copy(
                        isLoadingBalance = false,
                        balance =
                            data.balance,
                        accountNumber =
                            data.account_number,
                        error = null
                    )
            }


            result.onFailure { e ->

                _uiState.value =
                    _uiState.value.copy(
                        isLoadingBalance = false,
                        error =
                            e.message
                                ?: "Không thể tải thông tin ví."
                    )
            }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | LOOKUP RECIPIENT
    |--------------------------------------------------------------------------
    */

    fun lookupRecipient(
        accountNumber: String
    ) {

        val cleanAccountNumber =
            accountNumber
                .filter {
                    it.isDigit()
                }


        /*
        |--------------------------------------------------------------------------
        | VALIDATE
        |--------------------------------------------------------------------------
        */

        if (cleanAccountNumber.isBlank()) {

            lookupJob?.cancel()

            _recipient.value =
                null

            _uiState.value =
                _uiState.value.copy(
                    isLookingUpRecipient = false,
                    error =
                        "Vui lòng nhập số tài khoản người nhận."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | KHÔNG TRA CỨU CHÍNH MÌNH
        |--------------------------------------------------------------------------
        */

        if (
            cleanAccountNumber ==
            _uiState.value.accountNumber
        ) {

            lookupJob?.cancel()

            _recipient.value =
                null

            _uiState.value =
                _uiState.value.copy(
                    isLookingUpRecipient = false,
                    error =
                        "Bạn không thể chuyển tiền cho chính mình."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | HỦY REQUEST CŨ
        |--------------------------------------------------------------------------
        */

        lookupJob?.cancel()


        /*
        |--------------------------------------------------------------------------
        | REQUEST
        |--------------------------------------------------------------------------
        */

        lookupJob =
            viewModelScope.launch {

                _recipient.value =
                    null

                _uiState.value =
                    _uiState.value.copy(
                        isLookingUpRecipient = true,
                        error = null,
                        successMessage = null
                    )


                val result =
                    repository.getRecipient(
                        cleanAccountNumber
                    )


                result.onSuccess { data ->

                    /*
                    |--------------------------------------------------------------------------
                    | KIỂM TRA RESPONSE
                    |--------------------------------------------------------------------------
                    */

                    if (
                        data.account_number !=
                        cleanAccountNumber
                    ) {

                        _recipient.value =
                            null

                        _uiState.value =
                            _uiState.value.copy(
                                isLookingUpRecipient = false,
                                error =
                                    "Thông tin người nhận không hợp lệ."
                            )

                        return@onSuccess
                    }


                    _recipient.value =
                        data

                    _uiState.value =
                        _uiState.value.copy(
                            isLookingUpRecipient = false,
                            error = null
                        )
                }


                result.onFailure { e ->

                    _recipient.value =
                        null

                    _uiState.value =
                        _uiState.value.copy(
                            isLookingUpRecipient = false,
                            error =
                                e.message
                                    ?: "Không tìm thấy tài khoản người nhận."
                        )
                }
            }
    }


    /*
    |--------------------------------------------------------------------------
    | DEPOSIT
    |--------------------------------------------------------------------------
    */

    fun deposit(

        amount: Double,

        method: String,

        note: String?,

        onSuccess: (DepositData) -> Unit

    ) {

        /*
        |--------------------------------------------------------------------------
        | VALIDATE AMOUNT
        |--------------------------------------------------------------------------
        */

        if (amount <= 0) {

            _uiState.value =
                _uiState.value.copy(
                    error =
                        "Số tiền nạp phải lớn hơn 0."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | VALIDATE METHOD
        |--------------------------------------------------------------------------
        */

        if (
            method !in
            listOf(
                "bank",
                "card",
                "wallet"
            )
        ) {

            _uiState.value =
                _uiState.value.copy(
                    error =
                        "Phương thức nạp tiền không hợp lệ."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | REQUEST
        |--------------------------------------------------------------------------
        */

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isSubmitting = true,
                    error = null,
                    successMessage = null
                )


            val result =
                repository.deposit(
                    amount = amount,
                    method = method,
                    note = note
                )


            result.onSuccess { data ->

                _uiState.value =
                    _uiState.value.copy(
                        isSubmitting = false,
                        balance =
                            data.balance,
                        successMessage =
                            "Nạp tiền thành công.",
                        error = null
                    )


                onSuccess(
                    data
                )
            }


            result.onFailure { e ->

                _uiState.value =
                    _uiState.value.copy(
                        isSubmitting = false,
                        error =
                            e.message
                                ?: "Nạp tiền thất bại."
                    )
            }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | TRANSFER
    |--------------------------------------------------------------------------
    */

    fun transfer(

        receiver: String,

        amount: Double,

        note: String?,

        onSuccess: (TransferData) -> Unit

    ) {

        val cleanReceiver =
            receiver
                .filter {
                    it.isDigit()
                }


        /*
        |--------------------------------------------------------------------------
        | VALIDATE ACCOUNT NUMBER
        |--------------------------------------------------------------------------
        */

        if (cleanReceiver.isBlank()) {

            _uiState.value =
                _uiState.value.copy(
                    error =
                        "Vui lòng nhập số tài khoản người nhận."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | CHECK SELF TRANSFER
        |--------------------------------------------------------------------------
        */

        if (
            cleanReceiver ==
            _uiState.value.accountNumber
        ) {

            _uiState.value =
                _uiState.value.copy(
                    error =
                        "Bạn không thể chuyển tiền cho chính mình."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | CHECK RECIPIENT
        |--------------------------------------------------------------------------
        */

        val currentRecipient =
            _recipient.value


        if (currentRecipient == null) {

            _uiState.value =
                _uiState.value.copy(
                    error =
                        "Vui lòng kiểm tra tài khoản người nhận trước."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | ACCOUNT CHANGED
        |--------------------------------------------------------------------------
        */

        if (
            currentRecipient.account_number !=
            cleanReceiver
        ) {

            _recipient.value =
                null

            _uiState.value =
                _uiState.value.copy(
                    error =
                        "Số tài khoản đã thay đổi. Vui lòng kiểm tra lại người nhận."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | VALIDATE AMOUNT
        |--------------------------------------------------------------------------
        */

        if (amount <= 0) {

            _uiState.value =
                _uiState.value.copy(
                    error =
                        "Số tiền chuyển phải lớn hơn 0."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | CHECK BALANCE
        |--------------------------------------------------------------------------
        */

        if (
            amount >
            _uiState.value.balance
        ) {

            _uiState.value =
                _uiState.value.copy(
                    error =
                        "Số dư không đủ để thực hiện giao dịch."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | REQUEST
        |--------------------------------------------------------------------------
        */

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isSubmitting = true,
                    error = null,
                    successMessage = null
                )


            val result =
                repository.transfer(
                    receiver =
                        cleanReceiver,
                    amount =
                        amount,
                    note =
                        note
                )


            result.onSuccess { data ->

                _uiState.value =
                    _uiState.value.copy(
                        isSubmitting = false,
                        balance =
                            data.balance,
                        successMessage =
                            "Chuyển tiền thành công.",
                        error = null
                    )


                /*
                |--------------------------------------------------------------------------
                | XÓA NGƯỜI NHẬN SAU KHI CHUYỂN THÀNH CÔNG
                |--------------------------------------------------------------------------
                */

                _recipient.value =
                    null


                onSuccess(
                    data
                )
            }


            result.onFailure { e ->

                _uiState.value =
                    _uiState.value.copy(
                        isSubmitting = false,
                        error =
                            e.message
                                ?: "Chuyển tiền thất bại."
                    )
            }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | CLEAR RECIPIENT
    |--------------------------------------------------------------------------
    */

    fun clearRecipient() {

        lookupJob?.cancel()

        _recipient.value =
            null

        _uiState.value =
            _uiState.value.copy(
                isLookingUpRecipient = false
            )
    }


    /*
    |--------------------------------------------------------------------------
    | CLEAR MESSAGE
    |--------------------------------------------------------------------------
    */

    fun clearMessage() {

        _uiState.value =
            _uiState.value.copy(
                error = null,
                successMessage = null
            )
    }


    /*
    |--------------------------------------------------------------------------
    | CLEAR ERROR
    |--------------------------------------------------------------------------
    */

    fun clearError() {

        _uiState.value =
            _uiState.value.copy(
                error = null
            )
    }
}