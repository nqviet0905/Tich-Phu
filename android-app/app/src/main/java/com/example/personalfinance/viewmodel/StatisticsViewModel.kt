package com.example.personalfinance.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinance.data.model.CategoryStatisticData
import com.example.personalfinance.data.model.MonthlyStatisticData
import com.example.personalfinance.data.repository.StatisticsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar


data class StatisticsUiState(

    val isLoading: Boolean = false,

    val categoryData: CategoryStatisticData? = null,

    val monthlyData: MonthlyStatisticData? = null,

    val error: String? = null
)


class StatisticsViewModel : ViewModel() {

    private val repository =
        StatisticsRepository()


    private val _uiState =
        MutableStateFlow(
            StatisticsUiState()
        )


    val uiState: StateFlow<StatisticsUiState> =
        _uiState.asStateFlow()


    /*
    |--------------------------------------------------------------------------
    | THỜI GIAN HIỆN TẠI
    |--------------------------------------------------------------------------
    */

    private val calendar =
        Calendar.getInstance()


    var selectedMonth =
        calendar.get(
            Calendar.MONTH
        ) + 1
        private set


    var selectedYear =
        calendar.get(
            Calendar.YEAR
        )
        private set


    var selectedType =
        "expense"
        private set


    init {

        loadStatistics()
    }


    /*
    |--------------------------------------------------------------------------
    | LOAD STATISTICS
    |--------------------------------------------------------------------------
    */

    fun loadStatistics() {

        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(

                    isLoading = true,

                    error = null
                )


            /*
            |--------------------------------------------------------------------------
            | THỐNG KÊ THEO DANH MỤC
            |--------------------------------------------------------------------------
            */

            val categoryResult =
                repository
                    .getCategoryStatistics(

                        month =
                            selectedMonth,

                        year =
                            selectedYear,

                        type =
                            selectedType
                    )


            /*
            |--------------------------------------------------------------------------
            | THỐNG KÊ 12 THÁNG
            |--------------------------------------------------------------------------
            */

            val monthlyResult =
                repository
                    .getMonthlyStatistics(

                        year =
                            selectedYear
                    )


            val categoryData =
                categoryResult
                    .getOrNull()


            val monthlyData =
                monthlyResult
                    .getOrNull()


            /*
            |--------------------------------------------------------------------------
            | ERROR
            |--------------------------------------------------------------------------
            */

            if (
                categoryResult.isFailure ||
                monthlyResult.isFailure
            ) {

                _uiState.value =
                    _uiState.value.copy(

                        isLoading =
                            false,

                        categoryData =
                            categoryData,

                        monthlyData =
                            monthlyData,

                        error =
                            categoryResult
                                .exceptionOrNull()
                                ?.message
                                ?: monthlyResult
                                    .exceptionOrNull()
                                    ?.message
                                ?: "Không thể tải dữ liệu thống kê."
                    )

                return@launch
            }


            /*
            |--------------------------------------------------------------------------
            | SUCCESS
            |--------------------------------------------------------------------------
            */

            _uiState.value =
                StatisticsUiState(

                    isLoading =
                        false,

                    categoryData =
                        categoryData,

                    monthlyData =
                        monthlyData,

                    error =
                        null
                )
        }
    }


    /*
    |--------------------------------------------------------------------------
    | ĐỔI THÁNG
    |--------------------------------------------------------------------------
    */

    fun changeMonth(
        month: Int
    ) {

        if (month !in 1..12) {
            return
        }


        if (month == selectedMonth) {
            return
        }


        selectedMonth =
            month


        loadStatistics()
    }


    /*
    |--------------------------------------------------------------------------
    | THÁNG TRƯỚC
    |--------------------------------------------------------------------------
    */

    fun previousMonth() {

        if (selectedMonth == 1) {

            selectedMonth =
                12

            selectedYear--

        } else {

            selectedMonth--
        }


        loadStatistics()
    }


    /*
    |--------------------------------------------------------------------------
    | THÁNG SAU
    |--------------------------------------------------------------------------
    */

    fun nextMonth() {

        if (selectedMonth == 12) {

            selectedMonth =
                1

            selectedYear++

        } else {

            selectedMonth++
        }


        loadStatistics()
    }


    /*
    |--------------------------------------------------------------------------
    | ĐỔI NĂM
    |--------------------------------------------------------------------------
    */

    fun changeYear(
        year: Int
    ) {

        if (year !in 2000..2100) {
            return
        }


        if (year == selectedYear) {
            return
        }


        selectedYear =
            year


        loadStatistics()
    }


    /*
    |--------------------------------------------------------------------------
    | NĂM TRƯỚC
    |--------------------------------------------------------------------------
    */

    fun previousYear() {

        if (selectedYear <= 2000) {
            return
        }


        selectedYear--


        loadStatistics()
    }


    /*
    |--------------------------------------------------------------------------
    | NĂM SAU
    |--------------------------------------------------------------------------
    */

    fun nextYear() {

        if (selectedYear >= 2100) {
            return
        }


        selectedYear++


        loadStatistics()
    }


    /*
    |--------------------------------------------------------------------------
    | ĐỔI THU / CHI
    |--------------------------------------------------------------------------
    */

    fun changeType(
        type: String
    ) {

        if (
            type != "income" &&
            type != "expense"
        ) {
            return
        }


        if (type == selectedType) {
            return
        }


        selectedType =
            type


        loadStatistics()
    }


    /*
    |--------------------------------------------------------------------------
    | REFRESH
    |--------------------------------------------------------------------------
    */

    fun refresh() {

        loadStatistics()
    }
}