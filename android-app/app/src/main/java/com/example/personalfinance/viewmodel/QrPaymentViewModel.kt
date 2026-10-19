package com.example.personalfinance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.BankAccount
import com.example.personalfinance.data.model.Category
import com.example.personalfinance.data.model.QrPaymentData
import com.example.personalfinance.data.model.QrPaymentRequest
import com.example.personalfinance.data.model.QrPaymentResult
import com.example.personalfinance.data.repository.QrPaymentRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class QrPaymentUiState(

    val isLoading: Boolean = false,

    val isPaying: Boolean = false,

    val bankAccounts: List<BankAccount> =
        emptyList(),

    val categories: List<Category> =
        emptyList(),

    val selectedBankAccountId: Long? =
        null,

    val selectedCategoryId: Long? =
        null,

    val suggestedCategoryId: Long? =
        null,

    val error: String? =
        null,

    val paymentResult: QrPaymentResult? =
        null
)


class QrPaymentViewModel : ViewModel() {

    private val repository =
        QrPaymentRepository()


    private val _uiState =
        MutableStateFlow(
            QrPaymentUiState()
        )

    val uiState:
            StateFlow<QrPaymentUiState> =
        _uiState.asStateFlow()


    /*
    |--------------------------------------------------------------------------
    | LOAD PAYMENT DATA
    |--------------------------------------------------------------------------
    */

    fun loadPaymentData(
        merchantName: String
    ) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )


            try {

                coroutineScope {

                    /*
                     * Load song song để màn hình
                     * mở nhanh hơn.
                     */

                    val bankDeferred =
                        async {
                            repository
                                .getBankAccounts()
                        }

                    val categoryDeferred =
                        async {
                            repository
                                .getExpenseCategories()
                        }

                    val suggestionDeferred =
                        async {
                            repository
                                .getMerchantSuggestion(
                                    merchantName
                                )
                        }


                    val bankResult =
                        bankDeferred.await()

                    val categoryResult =
                        categoryDeferred.await()

                    val suggestionResult =
                        suggestionDeferred.await()


                    /*
                    |--------------------------------------------------------------------------
                    | BANK ACCOUNTS
                    |--------------------------------------------------------------------------
                    */

                    val accounts =
                        bankResult.getOrElse {

                            throw it
                        }


                    /*
                    |--------------------------------------------------------------------------
                    | CATEGORIES
                    |--------------------------------------------------------------------------
                    */

                    val categories =
                        categoryResult.getOrElse {

                            throw it
                        }


                    /*
                    |--------------------------------------------------------------------------
                    | DEFAULT BANK
                    |--------------------------------------------------------------------------
                    */

                    val defaultBank =
                        accounts.firstOrNull {
                            it.isDefault
                        } ?: accounts.firstOrNull()


                    /*
                    |--------------------------------------------------------------------------
                    | SUGGESTED CATEGORY
                    |--------------------------------------------------------------------------
                    */

                    val suggestion =
                        suggestionResult
                            .getOrNull()


                    val suggestedCategoryId =
                        suggestion
                            ?.categoryId
                            ?.takeIf { id ->

                                categories.any {
                                    it.id == id
                                }
                            }


                    _uiState.value =
                        _uiState.value.copy(

                            isLoading =
                                false,

                            bankAccounts =
                                accounts,

                            categories =
                                categories,

                            selectedBankAccountId =
                                defaultBank?.id,

                            selectedCategoryId =
                                suggestedCategoryId,

                            suggestedCategoryId =
                                suggestedCategoryId,

                            error =
                                null
                        )
                }

            } catch (e: Exception) {

                _uiState.value =
                    _uiState.value.copy(
                        isLoading = false,
                        error =
                            e.message
                                ?: "Không thể tải thông tin thanh toán."
                    )
            }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | SELECT BANK
    |--------------------------------------------------------------------------
    */

    fun selectBankAccount(
        id: Long
    ) {

        _uiState.value =
            _uiState.value.copy(
                selectedBankAccountId =
                    id
            )
    }


    /*
    |--------------------------------------------------------------------------
    | SELECT CATEGORY
    |--------------------------------------------------------------------------
    */

    fun selectCategory(
        id: Long
    ) {

        _uiState.value =
            _uiState.value.copy(
                selectedCategoryId =
                    id
            )
    }


    /*
    |--------------------------------------------------------------------------
    | PAY
    |--------------------------------------------------------------------------
    */

    fun pay(
        qrData: QrPaymentData,
        amount: Double,
        note: String? = null,
        onSuccess: (
            QrPaymentResult
        ) -> Unit
    ) {

        val state =
            _uiState.value


        /*
        |--------------------------------------------------------------------------
        | VALIDATE BANK
        |--------------------------------------------------------------------------
        */

        val bankAccountId =
            state.selectedBankAccountId

        if (bankAccountId == null) {

            _uiState.value =
                state.copy(
                    error =
                        "Vui lòng chọn tài khoản thanh toán."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | VALIDATE CATEGORY
        |--------------------------------------------------------------------------
        */

        val categoryId =
            state.selectedCategoryId

        if (categoryId == null) {

            _uiState.value =
                state.copy(
                    error =
                        "Vui lòng chọn mục đích thanh toán."
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
                state.copy(
                    error =
                        "Số tiền thanh toán không hợp lệ."
                )

            return
        }


        /*
        |--------------------------------------------------------------------------
        | CALL API
        |--------------------------------------------------------------------------
        */

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isPaying = true,
                    error = null
                )


            val request =
                QrPaymentRequest(

                    bankAccountId =
                        bankAccountId,

                    merchantName =
                        qrData.merchantName,

                    merchantBankCode =
                        qrData.bankCode,

                    merchantAccountNumber =
                        qrData.accountNumber,

                    categoryId =
                        categoryId,

                    amount =
                        amount,

                    note =
                        note
                            ?.trim()
                            ?.takeIf {
                                it.isNotBlank()
                            }
                )


            repository
                .payQr(
                    request
                )
                .onSuccess { result ->

                    _uiState.value =
                        _uiState.value.copy(
                            isPaying = false,
                            paymentResult = result,
                            error = null
                        )

                    onSuccess(
                        result
                    )
                }
                .onFailure { exception ->

                    _uiState.value =
                        _uiState.value.copy(
                            isPaying = false,
                            error =
                                exception.message
                                    ?: "Thanh toán thất bại."
                        )
                }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | GET SELECTED BANK
    |--------------------------------------------------------------------------
    */

    fun getSelectedBankAccount():
            BankAccount? {

        val state =
            _uiState.value

        return state.bankAccounts
            .firstOrNull {
                it.id ==
                        state.selectedBankAccountId
            }
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


    /*
    |--------------------------------------------------------------------------
    | RESET
    |--------------------------------------------------------------------------
    */

    fun reset() {

        _uiState.value =
            QrPaymentUiState()
    }
}