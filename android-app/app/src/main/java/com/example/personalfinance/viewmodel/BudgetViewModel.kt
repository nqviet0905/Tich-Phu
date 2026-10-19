package com.example.personalfinance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.Budget
import com.example.personalfinance.data.repository.BudgetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class BudgetUiState(
    val isLoading: Boolean = false,
    val budgets: List<Budget> = emptyList(),
    val totalBudget: Double = 0.0,
    val totalSpent: Double = 0.0,
    val totalRemaining: Double = 0.0,
    val error: String? = null
)

class BudgetViewModel : ViewModel() {

    private val _selectedBudget =
        MutableStateFlow<Budget?>(null)

    val selectedBudget: StateFlow<Budget?> =
        _selectedBudget.asStateFlow()

    private val _message =
        MutableStateFlow<String?>(null)

    val message: StateFlow<String?> =
        _message.asStateFlow()

    private val repository =
        BudgetRepository()

    private val _uiState =
        MutableStateFlow(
            BudgetUiState()
        )

    val uiState: StateFlow<BudgetUiState> =
        _uiState.asStateFlow()

    init {
        loadBudgets()
    }

    fun loadBudgetDetail(
        id: Long
    ) {

        viewModelScope.launch {

            val result =
                repository.getBudgetDetail(id)

            result.onSuccess {
                _selectedBudget.value = it
            }

            result.onFailure {
                _message.value =
                    it.message
                        ?: "Không thể tải ngân sách."
            }
        }
    }

    fun updateBudget(
        id: Long,
        amount: Double,
        warningLevel: Int,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            val result =
                repository.updateBudget(
                    id,
                    amount,
                    warningLevel
                )

            result.onSuccess { msg ->

                _message.value = msg

                loadBudgets()

                onSuccess()
            }

            result.onFailure {

                _message.value =
                    it.message
                        ?: "Cập nhật thất bại."
            }
        }
    }

    fun deleteBudget(
        id: Long,
        onSuccess: () -> Unit = {}
    ) {

        viewModelScope.launch {

            val result =
                repository.deleteBudget(id)

            result.onSuccess {

                loadBudgets()

                onSuccess()
            }

            result.onFailure {

                _message.value =
                    it.message
                        ?: "Xóa ngân sách thất bại."
            }
        }
    }

    fun createBudget(
        categoryId: Long,
        amount: Double,
        month: Int,
        year: Int,
        warningLevel: Int,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            val result =
                repository.createBudget(
                    categoryId = categoryId,
                    amount = amount,
                    month = month,
                    year = year,
                    warningLevel = warningLevel
                )

            result.onSuccess { msg ->

                _message.value = msg

                loadBudgets()

                onSuccess()
            }

            result.onFailure { e ->

                _message.value =
                    e.message
                        ?: "Có lỗi xảy ra."
            }
        }
    }

    fun loadBudgets() {

        val calendar =
            Calendar.getInstance()

        val month =
            calendar.get(Calendar.MONTH) + 1

        val year =
            calendar.get(Calendar.YEAR)

        viewModelScope.launch {

            _uiState.value =
                BudgetUiState(
                    isLoading = true
                )

            val result =
                repository.getBudgets(
                    month,
                    year
                )

            result.onSuccess { data ->

                _uiState.value =
                    BudgetUiState(
                        budgets =
                            data?.budgets
                                ?: emptyList(),

                        totalBudget =
                            data?.summary
                                ?.total_budget
                                ?: 0.0,

                        totalSpent =
                            data?.summary
                                ?.total_spent
                                ?: 0.0,

                        totalRemaining =
                            data?.summary
                                ?.total_remaining
                                ?: 0.0
                    )
            }

            result.onFailure {

                _uiState.value =
                    BudgetUiState(
                        error =
                            it.message
                                ?: "Có lỗi xảy ra."
                    )
            }
        }
    }
}