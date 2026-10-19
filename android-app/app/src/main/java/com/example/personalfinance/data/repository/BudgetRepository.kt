package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.BudgetListData
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.example.personalfinance.data.model.CreateBudgetRequest
import com.example.personalfinance.data.model.Budget
import com.example.personalfinance.data.model.UpdateBudgetRequest

class BudgetRepository {

    suspend fun getBudgetDetail(
        id: Long
    ): Result<Budget?> {

        return try {

            val user =
                FirebaseAuth.getInstance().currentUser
                    ?: return Result.failure(
                        Exception("Chưa đăng nhập.")
                    )

            val token =
                user.getIdToken(false)
                    .await()
                    .token
                    ?: return Result.failure(
                        Exception("Không lấy được token.")
                    )

            val response =
                RetrofitClient.apiService
                    .getBudgetDetail(
                        "Bearer $token",
                        id
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
                            ?: "Không thể lấy ngân sách."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBudget(
        id: Long,
        amount: Double,
        warningLevel: Int
    ): Result<String> {

        return try {

            val user =
                FirebaseAuth.getInstance().currentUser
                    ?: return Result.failure(
                        Exception("Chưa đăng nhập.")
                    )

            val token =
                user.getIdToken(false)
                    .await()
                    .token
                    ?: return Result.failure(
                        Exception("Không lấy được token.")
                    )

            val response =
                RetrofitClient.apiService
                    .updateBudget(
                        authorization = "Bearer $token",
                        request = UpdateBudgetRequest(
                            id = id,
                            amount = amount,
                            warning_level = warningLevel
                        )
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {
                Result.success(
                    response.body()?.message
                        ?: "Cập nhật ngân sách thành công."
                )
            } else {
                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể cập nhật ngân sách."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBudget(
        id: Long
    ): Result<String> {

        return try {

            val user =
                FirebaseAuth.getInstance().currentUser
                    ?: return Result.failure(
                        Exception("Chưa đăng nhập.")
                    )

            val token =
                user.getIdToken(false)
                    .await()
                    .token
                    ?: return Result.failure(
                        Exception("Không lấy được token.")
                    )

            val response =
                RetrofitClient.apiService
                    .deleteBudget(
                        "Bearer $token",
                        id
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {
                Result.success(
                    response.body()?.message
                        ?: "Xóa ngân sách thành công."
                )
            } else {
                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể xóa ngân sách."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createBudget(
        categoryId: Long,
        amount: Double,
        month: Int,
        year: Int,
        warningLevel: Int
    ): Result<String> {

        return try {

            val user =
                FirebaseAuth.getInstance().currentUser
                    ?: return Result.failure(
                        Exception("Chưa đăng nhập.")
                    )

            val token =
                user.getIdToken(false)
                    .await()
                    .token
                    ?: return Result.failure(
                        Exception("Không lấy được token.")
                    )

            val response =
                RetrofitClient.apiService.createBudget(
                    authorization = "Bearer $token",
                    request = CreateBudgetRequest(
                        category_id = categoryId,
                        amount = amount,
                        month = month,
                        year = year,
                        warning_level = warningLevel
                    )
                )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Tạo ngân sách thành công."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể tạo ngân sách."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBudgets(
        month: Int,
        year: Int
    ): Result<BudgetListData?> {

        return try {

            val user =
                FirebaseAuth.getInstance().currentUser
                    ?: return Result.failure(
                        Exception("Chưa đăng nhập.")
                    )

            val token =
                user.getIdToken(false)
                    .await()
                    .token
                    ?: return Result.failure(
                        Exception("Không lấy được token.")
                    )

            val response =
                RetrofitClient.apiService
                    .getBudgets(
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
                            ?: "Không thể lấy ngân sách."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}