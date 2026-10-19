package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.Goal
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.example.personalfinance.data.model.CreateGoalRequest
import com.example.personalfinance.data.model.AddContributionRequest
import com.example.personalfinance.data.model.GoalContribution
import com.example.personalfinance.data.model.UpdateGoalRequest

class GoalRepository {

    suspend fun updateGoal(
        id: Long,
        name: String,
        description: String?,
        targetAmount: Double,
        startDate: String,
        deadline: String,
        status: String
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
                    .updateGoal(
                        authorization = "Bearer $token",
                        request = UpdateGoalRequest(
                            id = id,
                            name = name,
                            description = description,
                            target_amount = targetAmount,
                            start_date = startDate,
                            deadline = deadline,
                            status = status
                        )
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Cập nhật mục tiêu thành công."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể cập nhật mục tiêu."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteGoal(
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
                    .deleteGoal(
                        authorization = "Bearer $token",
                        id = id
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Hủy mục tiêu thành công."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể hủy mục tiêu."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGoalDetail(
        id: Long
    ): Result<Goal?> {

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
                    .getGoalDetail(
                        authorization = "Bearer $token",
                        id = id
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
                            ?: "Không thể lấy chi tiết mục tiêu."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getContributions(
        goalId: Long
    ): Result<List<GoalContribution>> {

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
                    .getGoalContributions(
                        authorization = "Bearer $token",
                        goalId = goalId
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.data
                        ?: emptyList()
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể lấy lịch sử đóng góp."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addContribution(
        goalId: Long,
        amount: Double,
        contributionDate: String,
        note: String?
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
                    .addGoalContribution(
                        authorization = "Bearer $token",
                        request = AddContributionRequest(
                            goal_id = goalId,
                            amount = amount,
                            contribution_date =
                                contributionDate,
                            note = note
                        )
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Đóng góp thành công."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể thêm đóng góp."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createGoal(
        name: String,
        description: String?,
        targetAmount: Double,
        currentAmount: Double,
        startDate: String,
        deadline: String
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
                RetrofitClient.apiService.createGoal(
                    authorization = "Bearer $token",
                    request = CreateGoalRequest(
                        name = name,
                        description = description,
                        target_amount = targetAmount,
                        current_amount = currentAmount,
                        start_date = startDate,
                        deadline = deadline
                    )
                )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Tạo mục tiêu thành công."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể tạo mục tiêu."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun getGoals(
        status: String? = null
    ): Result<List<Goal>> {

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
                    .getGoals(
                        authorization = "Bearer $token",
                        status = status
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.data
                        ?: emptyList()
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể lấy danh sách mục tiêu."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}