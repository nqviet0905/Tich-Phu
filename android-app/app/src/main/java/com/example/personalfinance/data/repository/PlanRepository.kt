package com.example.personalfinance.data.repository

import com.example.personalfinance.data.model.FinancialPlan
import com.example.personalfinance.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import com.example.personalfinance.data.model.CreatePlanRequest
import com.example.personalfinance.data.model.AdjustPlanRequest
import com.example.personalfinance.data.model.PlanAllocationRequest
import com.example.personalfinance.data.model.PlanDetailData

class PlanRepository {

    suspend fun getPlanDetail(
        id: Long
    ): Result<PlanDetailData?> {

        return try {

            val user = FirebaseAuth.getInstance().currentUser
                ?: return Result.failure(
                    Exception("Chưa đăng nhập.")
                )

            val token = user
                .getIdToken(false)
                .await()
                .token
                ?: return Result.failure(
                    Exception("Không lấy được token.")
                )

            val response =
                RetrofitClient.apiService.getPlanDetail(
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
                            ?: "Không thể lấy chi tiết kế hoạch."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun adjustPlan(
        planId: Long,
        allocations: List<PlanAllocationRequest>
    ): Result<String> {

        return try {

            val user = FirebaseAuth.getInstance().currentUser
                ?: return Result.failure(
                    Exception("Chưa đăng nhập.")
                )

            val token = user
                .getIdToken(false)
                .await()
                .token
                ?: return Result.failure(
                    Exception("Không lấy được token.")
                )

            val response =
                RetrofitClient.apiService.adjustPlan(
                    authorization = "Bearer $token",
                    request = AdjustPlanRequest(
                        plan_id = planId,
                        allocations = allocations
                    )
                )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {
                Result.success(
                    response.body()?.message
                        ?: "Cập nhật phân bổ thành công."
                )
            } else {
                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể cập nhật phân bổ."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPlan(
        planName: String?,
        monthlyIncome: Double,
        fixedExpense: Double,
        savingTarget: Double,
        month: Int,
        year: Int
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
                    .createPlan(
                        authorization =
                            "Bearer $token",

                        request =
                            CreatePlanRequest(
                                plan_name =
                                    planName,

                                month =
                                    month,

                                year =
                                    year,

                                monthly_income =
                                    monthlyIncome,

                                fixed_expense =
                                    fixedExpense,

                                saving_target =
                                    savingTarget
                            )
                    )

            if (
                response.isSuccessful &&
                response.body()?.success == true
            ) {

                Result.success(
                    response.body()?.message
                        ?: "Tạo kế hoạch thành công."
                )

            } else {

                Result.failure(
                    Exception(
                        response.body()?.message
                            ?: "Không thể tạo kế hoạch."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPlans(
        year: Int? = null,
        status: String? = null
    ): Result<List<FinancialPlan>> {

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
                    .getPlans(
                        authorization = "Bearer $token",
                        year = year,
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
                            ?: "Không thể lấy kế hoạch tài chính."
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}