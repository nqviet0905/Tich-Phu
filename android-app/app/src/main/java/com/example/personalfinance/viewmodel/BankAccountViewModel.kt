package com.example.personalfinance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.BankAccount
import com.example.personalfinance.data.repository.BankAccountRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class BankAccountUiState(

    val isLoading: Boolean = false,

    val accounts: List<BankAccount> =
        emptyList(),

    val error: String? = null,

    val successMessage: String? = null
)


class BankAccountViewModel : ViewModel() {

    private val repository =
        BankAccountRepository()


    private val _uiState =
        MutableStateFlow(
            BankAccountUiState()
        )

    val uiState:
            StateFlow<BankAccountUiState> =
        _uiState.asStateFlow()


    /*
    |--------------------------------------------------------------------------
    | LOAD
    |--------------------------------------------------------------------------
    */

    fun loadAccounts() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null
                )


            repository
                .getBankAccounts()
                .onSuccess { accounts ->

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            accounts = accounts,
                            error = null
                        )
                }
                .onFailure { exception ->

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error =
                                exception.message
                                    ?: "Đã xảy ra lỗi."
                        )
                }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | CREATE
    |--------------------------------------------------------------------------
    */

    fun linkBankAccount(
        bankCode: String,
        bankName: String,
        accountNumber: String,
        accountName: String,
        isDefault: Boolean = false,
        onSuccess: (() -> Unit)? = null
    ) {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    error = null,
                    successMessage = null
                )


            repository
                .createBankAccount(
                    bankCode =
                        bankCode,
                    bankName =
                        bankName,
                    accountNumber =
                        accountNumber,
                    accountName =
                        accountName,
                    isDefault =
                        isDefault
                )
                .onSuccess {

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            successMessage =
                                "Liên kết tài khoản thành công."
                        )

                    loadAccounts()

                    onSuccess?.invoke()
                }
                .onFailure { exception ->

                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error =
                                exception.message
                                    ?: "Liên kết thất bại."
                        )
                }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | SET DEFAULT
    |--------------------------------------------------------------------------
    */

    fun setDefault(
        bankAccountId: Long
    ) {

        viewModelScope.launch {

            repository
                .setDefaultBankAccount(
                    bankAccountId
                )
                .onSuccess {

                    _uiState.value =
                        _uiState.value.copy(
                            successMessage =
                                "Đã đổi tài khoản thanh toán mặc định."
                        )

                    loadAccounts()
                }
                .onFailure { exception ->

                    _uiState.value =
                        _uiState.value.copy(
                            error =
                                exception.message
                                    ?: "Không thể đổi tài khoản mặc định."
                        )
                }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | DELETE
    |--------------------------------------------------------------------------
    */

    fun unlinkBankAccount(
        bankAccountId: Long
    ) {

        viewModelScope.launch {

            repository
                .deleteBankAccount(
                    bankAccountId
                )
                .onSuccess {

                    _uiState.value =
                        _uiState.value.copy(
                            successMessage =
                                "Đã hủy liên kết tài khoản."
                        )

                    loadAccounts()
                }
                .onFailure { exception ->

                    _uiState.value =
                        _uiState.value.copy(
                            error =
                                exception.message
                                    ?: "Không thể hủy liên kết."
                        )
                }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | DEFAULT ACCOUNT
    |--------------------------------------------------------------------------
    */

    fun getDefaultAccount():
            BankAccount? {

        return _uiState.value
            .accounts
            .firstOrNull {
                it.isDefault
            }
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
}