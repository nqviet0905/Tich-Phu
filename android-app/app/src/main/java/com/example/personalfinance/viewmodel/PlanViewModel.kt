package com.example.personalfinance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.FinancialPlan
import com.example.personalfinance.data.repository.PlanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.personalfinance.data.model.PlanDetailData
import com.example.personalfinance.data.model.PlanAllocationRequest
import com.example.personalfinance.data.model.Category
import com.example.personalfinance.data.repository.CategoryRepository

data class PlanUiState(
    val isLoading: Boolean = false,
    val plans: List<FinancialPlan> = emptyList(),
    val error: String? = null
)

class PlanViewModel : ViewModel() {

    private val categoryRepository =
        CategoryRepository()

    private val _expenseCategories =
        MutableStateFlow<List<Category>>(
            emptyList()
        )

    val expenseCategories:
            StateFlow<List<Category>> =
        _expenseCategories.asStateFlow()
    private val _selectedPlan =
        MutableStateFlow<PlanDetailData?>(null)

    val selectedPlan: StateFlow<PlanDetailData?> =
        _selectedPlan.asStateFlow()
    private val _message =
        MutableStateFlow<String?>(null)

    val message: StateFlow<String?> =
        _message.asStateFlow()

    private val repository =
        PlanRepository()

    private val _uiState =
        MutableStateFlow(
            PlanUiState()
        )

    val uiState: StateFlow<PlanUiState> =
        _uiState.asStateFlow()

    init {
        loadPlans()
    }

    fun loadExpenseCategories() {

        viewModelScope.launch {

            val result =
                categoryRepository
                    .getCategories("expense")

            result.onSuccess {
                _expenseCategories.value = it
            }

            result.onFailure {

                _message.value =
                    it.message
                        ?: "Không thể tải danh mục chi tiêu."
            }
        }
    }

    fun loadPlanDetail(
        id: Long
    ) {

        viewModelScope.launch {

            val result =
                repository.getPlanDetail(id)

            result.onSuccess {
                _selectedPlan.value = it
            }

            result.onFailure {
                _message.value =
                    it.message
                        ?: "Không thể tải kế hoạch."
            }
        }
    }

    fun adjustPlan(
        planId: Long,
        allocations: List<PlanAllocationRequest>,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            val result =
                repository.adjustPlan(
                    planId,
                    allocations
                )

            result.onSuccess { msg ->

                _message.value = msg

                loadPlanDetail(planId)

                onSuccess()
            }

            result.onFailure {

                _message.value =
                    it.message
                        ?: "Cập nhật phân bổ thất bại."
            }
        }
    }

    fun createPlan(
        planName: String?,
        monthlyIncome: Double,
        fixedExpense: Double,
        savingTarget: Double,
        month: Int,
        year: Int,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            val result =
                repository.createPlan(
                    planName = planName,
                    monthlyIncome =
                        monthlyIncome,
                    fixedExpense =
                        fixedExpense,
                    savingTarget =
                        savingTarget,
                    month = month,
                    year = year
                )

            result.onSuccess { msg ->

                _message.value = msg

                loadPlans()

                onSuccess()
            }

            result.onFailure {

                _message.value =
                    it.message
                        ?: "Tạo kế hoạch thất bại."
            }
        }
    }
    fun loadPlans() {

        viewModelScope.launch {

            _uiState.value =
                PlanUiState(
                    isLoading = true
                )

            val result =
                repository.getPlans()

            result.onSuccess { plans ->

                _uiState.value =
                    PlanUiState(
                        plans = plans
                    )
            }

            result.onFailure { e ->

                _uiState.value =
                    PlanUiState(
                        error =
                            e.message
                                ?: "Có lỗi xảy ra."
                    )
            }
        }
    }
}