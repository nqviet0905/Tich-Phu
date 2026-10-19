package com.example.personalfinance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.Transaction
import com.example.personalfinance.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.personalfinance.data.model.Category
import com.example.personalfinance.data.repository.CategoryRepository

data class TransactionUiState(
    val isLoading: Boolean = false,
    val transactions: List<Transaction> = emptyList(),
    val error: String? = null
)

class TransactionViewModel : ViewModel() {

    private val _selectedTransaction =
        MutableStateFlow<Transaction?>(null)

    val selectedTransaction:
            StateFlow<Transaction?> =
        _selectedTransaction.asStateFlow()

    private val repository =
        TransactionRepository()

    private val _uiState =
        MutableStateFlow(
            TransactionUiState()
        )

    private val categoryRepository =
        CategoryRepository()

    private val _categories =
        MutableStateFlow<List<Category>>(
            emptyList()
        )

    val categories:
            StateFlow<List<Category>> =
        _categories.asStateFlow()

    var createMessage = MutableStateFlow<String?>(null)
        private set

    val uiState: StateFlow<TransactionUiState> =
        _uiState.asStateFlow()

    init {
        loadTransactions()
    }

    fun deleteTransaction(
        id: Long,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            val result =
                repository
                    .deleteTransaction(id)

            result.onSuccess {

                loadTransactions()

                onSuccess()
            }

            result.onFailure {

                setCreateMessage(
                    it.message
                        ?: "Xóa giao dịch thất bại."
                )
            }
        }
    }

    fun updateTransaction(
        id: Long,
        categoryId: Long,
        type: String,
        amount: Double,
        transactionDate: String,
        note: String?,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            val result =
                repository.updateTransaction(
                    id,
                    categoryId,
                    type,
                    amount,
                    transactionDate,
                    note
                )

            result.onSuccess {

                loadTransactions()

                onSuccess()
            }

            result.onFailure {

                setCreateMessage(
                    it.message
                        ?: "Cập nhật thất bại."
                )
            }
        }
    }

    fun loadTransactionDetail(
        id: Long
    ) {

        viewModelScope.launch {

            val result =
                repository
                    .getTransactionDetail(id)

            result.onSuccess {
                _selectedTransaction.value = it
            }

            result.onFailure {
                setCreateMessage(
                    it.message
                        ?: "Không thể tải giao dịch."
                )
            }
        }
    }

    fun loadCategories(
        type: String
    ) {

        viewModelScope.launch {

            val result =
                categoryRepository
                    .getCategories(type)

            result.onSuccess {
                _categories.value = it
            }

            result.onFailure { e ->

                setCreateMessage(
                    e.message
                        ?: "Không thể tải danh mục."
                )
            }
        }
    }

    fun setCreateMessage(
        message: String?
    ) {
        createMessage.value = message
    }
    fun createTransaction(
        categoryId: Long,
        type: String,
        amount: Double,
        transactionDate: String,
        note: String?,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            val result =
                repository.createTransaction(
                    categoryId = categoryId,
                    type = type,
                    amount = amount,
                    transactionDate = transactionDate,
                    note = note
                )

            result.onSuccess { message ->

                createMessage.value = message

                loadTransactions()

                onSuccess()
            }

            result.onFailure { e ->

                createMessage.value =
                    e.message
                        ?: "Có lỗi xảy ra."
            }
        }
    }

    fun loadTransactions() {

        viewModelScope.launch {

            _uiState.value =
                TransactionUiState(
                    isLoading = true
                )

            val result =
                repository.getTransactions()

            result.onSuccess { data ->

                _uiState.value =
                    TransactionUiState(
                        transactions =
                            data?.items
                                ?: emptyList()
                    )
            }

            result.onFailure { e ->

                _uiState.value =
                    TransactionUiState(
                        error =
                            e.message
                                ?: "Có lỗi xảy ra."
                    )
            }
        }
    }
}