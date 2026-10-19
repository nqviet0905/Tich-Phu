package com.example.personalfinance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.Goal
import com.example.personalfinance.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.personalfinance.data.model.GoalContribution

data class GoalUiState(
    val isLoading: Boolean = false,
    val goals: List<Goal> = emptyList(),
    val error: String? = null
)

class GoalViewModel : ViewModel() {

    private val _selectedGoal =
        MutableStateFlow<Goal?>(null)

    val selectedGoal: StateFlow<Goal?> =
        _selectedGoal.asStateFlow()

    private val _contributions =
        MutableStateFlow<List<GoalContribution>>(
            emptyList()
        )

    val contributions:
            StateFlow<List<GoalContribution>> =
        _contributions.asStateFlow()

    private val _message =
        MutableStateFlow<String?>(null)

    val message: StateFlow<String?> =
        _message.asStateFlow()
    private val repository =
        GoalRepository()

    private val _uiState =
        MutableStateFlow(
            GoalUiState()
        )

    val uiState: StateFlow<GoalUiState> =
        _uiState.asStateFlow()

    init {
        loadGoals()
    }

    fun updateGoal(
        id: Long,
        name: String,
        description: String?,
        targetAmount: Double,
        startDate: String,
        deadline: String,
        status: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            repository.updateGoal(
                id = id,
                name = name,
                description = description,
                targetAmount = targetAmount,
                startDate = startDate,
                deadline = deadline,
                status = status
            ).onSuccess { msg ->

                _message.value = msg

                loadGoals()
                loadGoalDetail(id)

                onSuccess()

            }.onFailure {

                _message.value =
                    it.message
                        ?: "Cập nhật mục tiêu thất bại."
            }
        }
    }

    fun deleteGoal(
        id: Long,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            repository.deleteGoal(id)
                .onSuccess {

                    loadGoals()

                    onSuccess()
                }
                .onFailure {

                    _message.value =
                        it.message
                            ?: "Không thể hủy mục tiêu."
                }
        }
    }
    fun loadGoalDetail(
        id: Long
    ) {

        viewModelScope.launch {

            repository
                .getGoalDetail(id)
                .onSuccess {
                    _selectedGoal.value = it
                }
                .onFailure {
                    _message.value =
                        it.message
                }
        }
    }

    fun loadContributions(
        goalId: Long
    ) {

        viewModelScope.launch {

            repository
                .getContributions(goalId)
                .onSuccess {
                    _contributions.value = it
                }
                .onFailure {
                    _message.value =
                        it.message
                }
        }
    }

    fun addContribution(
        goalId: Long,
        amount: Double,
        contributionDate: String,
        note: String?,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            repository
                .addContribution(
                    goalId,
                    amount,
                    contributionDate,
                    note
                )
                .onSuccess {

                    loadGoalDetail(goalId)
                    loadContributions(goalId)
                    loadGoals()

                    onSuccess()
                }
                .onFailure {

                    _message.value =
                        it.message
                }
        }
    }

    fun createGoal(
        name: String,
        description: String?,
        targetAmount: Double,
        currentAmount: Double,
        startDate: String,
        deadline: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            val result =
                repository.createGoal(
                    name = name,
                    description = description,
                    targetAmount = targetAmount,
                    currentAmount = currentAmount,
                    startDate = startDate,
                    deadline = deadline
                )

            result.onSuccess { msg ->

                _message.value = msg

                loadGoals()

                onSuccess()
            }

            result.onFailure {

                _message.value =
                    it.message
                        ?: "Tạo mục tiêu thất bại."
            }
        }
    }
    fun loadGoals() {

        viewModelScope.launch {

            _uiState.value =
                GoalUiState(
                    isLoading = true
                )

            val result =
                repository.getGoals()

            result.onSuccess { goals ->

                _uiState.value =
                    GoalUiState(
                        goals = goals
                    )
            }

            result.onFailure { e ->

                _uiState.value =
                    GoalUiState(
                        error =
                            e.message
                                ?: "Có lỗi xảy ra."
                    )
            }
        }
    }
}