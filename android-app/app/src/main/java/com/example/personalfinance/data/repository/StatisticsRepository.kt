package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.CategoryStatisticData
import com.example.personalfinance.data.model.MonthlyStatisticData
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class StatisticsRepository {

    private suspend fun getToken(): String {

        val user =
            FirebaseAuth.getInstance().currentUser
                ?: throw Exception("Chưa đăng nhập.")

        return user
            .getIdToken(false)
            .await()
            .token
            ?: throw Exception("Không lấy được token.")
    }

    suspend fun getCategoryStatistics(
        month: Int,
        year: Int,
        type: String
    ): Result<CategoryStatisticData?> {

        return try {

            val token = getToken()

            val response =
                RetrofitClient.apiService
                    .getCategoryStatistics(
                        authorization = "Bearer $token",
                        month = month,
                        year = year,
                        type = type
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.data
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể tải thống kê danh mục."
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    suspend fun getMonthlyStatistics(
        year: Int
    ): Result<MonthlyStatisticData?> {

        return try {

            val token = getToken()

            val response =
                RetrofitClient.apiService
                    .getMonthlyStatistics(
                        authorization = "Bearer $token",
                        year = year
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.data
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể tải thống kê theo tháng."
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}