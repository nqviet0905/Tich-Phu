package com.example.personalfinance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.OverviewData
import com.example.personalfinance.data.repository.DashboardRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar


data class DashboardUiState(

    val isLoading: Boolean = false,

    val data: OverviewData? = null,

    val error: String? = null
)


class DashboardViewModel : ViewModel() {

    private val repository =
        DashboardRepository()


    private val _uiState =
        MutableStateFlow(
            DashboardUiState()
        )


    val uiState: StateFlow<DashboardUiState> =
        _uiState.asStateFlow()


    init {
        loadOverview()
    }


    fun loadOverview() {

        val calendar =
            Calendar.getInstance()


        val month =
            calendar.get(
                Calendar.MONTH
            ) + 1


        val year =
            calendar.get(
                Calendar.YEAR
            )


        viewModelScope.launch {

            /*
            |--------------------------------------------------------------------------
            | LOADING
            |--------------------------------------------------------------------------
            */

            _uiState.value =
                _uiState.value.copy(

                    isLoading =
                        _uiState.value.data == null,

                    error =
                        null
                )


            val result =
                repository.getOverview(
                    month = month,
                    year = year
                )


            /*
            |--------------------------------------------------------------------------
            | SUCCESS
            |--------------------------------------------------------------------------
            */

            result.onSuccess { data ->

                _uiState.value =
                    _uiState.value.copy(

                        isLoading =
                            false,

                        data =
                            data,

                        error =
                            null
                    )
            }


            /*
            |--------------------------------------------------------------------------
            | ERROR
            |--------------------------------------------------------------------------
            */

            result.onFailure { e ->

                _uiState.value =
                    _uiState.value.copy(

                        isLoading =
                            false,

                        error =
                            e.message
                                ?: "Có lỗi xảy ra."
                    )
            }
        }
    }


    fun refresh() {

        loadOverview()
    }
}