package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.OverviewData
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class DashboardRepository {

    suspend fun getOverview(
        month: Int,
        year: Int
    ): Result<OverviewData?> {

        return try {

            val firebaseUser =
                FirebaseAuth.getInstance().currentUser
                    ?: return Result.failure(
                        Exception("Chưa đăng nhập.")
                    )

            val token =
                firebaseUser.getIdToken(false).await().token
                    ?: return Result.failure(
                        Exception("Không lấy được token.")
                    )

            val response =
                RetrofitClient.apiService.getOverview(
                    authorization = "Bearer $token",
                    month = month,
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
                            ?: "Không thể tải Dashboard."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}