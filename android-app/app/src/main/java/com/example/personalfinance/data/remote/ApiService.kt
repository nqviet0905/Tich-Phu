package com.example.personalfinance.data.remote

import com.example.personalfinance.data.model.OverviewData
import com.example.personalfinance.data.model.Budget
import com.example.personalfinance.data.model.ApiResponse
import com.example.personalfinance.data.model.SyncUserRequest
import com.example.personalfinance.data.model.User
import com.example.personalfinance.data.model.TransactionListData
import com.example.personalfinance.data.model.CreateTransactionRequest
import com.example.personalfinance.data.model.Transaction
import com.example.personalfinance.data.model.Category
import com.example.personalfinance.data.model.UpdateTransactionRequest
import com.example.personalfinance.data.model.BudgetListData
import com.example.personalfinance.data.model.CreateBudgetRequest
import com.example.personalfinance.data.model.UpdateBudgetRequest
import com.example.personalfinance.data.model.FinancialPlan
import com.example.personalfinance.data.model.CreatePlanRequest
import com.example.personalfinance.data.model.AdjustPlanRequest
import com.example.personalfinance.data.model.PlanDetailData
import com.example.personalfinance.data.model.Goal
import com.example.personalfinance.data.model.CreateGoalRequest
import com.example.personalfinance.data.model.GoalContribution
import com.example.personalfinance.data.model.AddContributionRequest
import com.example.personalfinance.data.model.UpdateGoalRequest
import com.example.personalfinance.data.model.CategoryStatisticData
import com.example.personalfinance.data.model.MonthlyStatisticData
import com.example.personalfinance.data.model.AppNotification
import com.example.personalfinance.data.model.ReadNotificationRequest
import com.example.personalfinance.data.model.RegisterDeviceTokenRequest
import com.example.personalfinance.data.model.DepositRequest
import com.example.personalfinance.data.model.DepositData
import com.example.personalfinance.data.model.TransferRequest
import com.example.personalfinance.data.model.TransferData
import com.example.personalfinance.data.model.WalletBalanceData
import com.example.personalfinance.data.model.RecipientData
import com.example.personalfinance.data.model.BankAccount
import com.example.personalfinance.data.model.BankAccountActionData
import com.example.personalfinance.data.model.BankAccountActionRequest
import com.example.personalfinance.data.model.CreateBankAccountRequest
import com.example.personalfinance.data.model.QrPaymentRequest
import com.example.personalfinance.data.model.QrPaymentResult
import com.example.personalfinance.data.model.MerchantCategorySuggestion



import retrofit2.http.PUT
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.DELETE



interface ApiService {

    @GET("api/auth/test-auth.php")
    suspend fun testAuth(
        @Header("Authorization") authorization: String
    ): Response<ApiResponse<User>>

    @POST("api/auth/sync-user.php")
    suspend fun syncUser(
        @Header("Authorization") authorization: String,
        @Body request: SyncUserRequest
    ): Response<ApiResponse<User>>

    @GET("api/statistics/overview.php")
    suspend fun getOverview(
        @Header("Authorization") authorization: String,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<ApiResponse<OverviewData>>

    @GET("api/transactions/list.php")
    suspend fun getTransactions(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50,
        @Query("type") type: String? = null,
        @Query("month") month: Int? = null,
        @Query("year") year: Int? = null
    ): Response<ApiResponse<TransactionListData>>

    @POST("api/transactions/create.php")
    suspend fun createTransaction(
        @Header("Authorization") authorization: String,
        @Body request: CreateTransactionRequest
    ): Response<ApiResponse<Any>>

    @GET("api/categories/list.php")
    suspend fun getCategories(
        @Header("Authorization") authorization: String,
        @Query("type") type: String
    ): Response<ApiResponse<List<Category>>>

    @GET("api/transactions/detail.php")
    suspend fun getTransactionDetail(
        @Header("Authorization") authorization: String,
        @Query("id") id: Long
    ): Response<ApiResponse<Transaction>>

    @PUT("api/transactions/update.php")
    suspend fun updateTransaction(
        @Header("Authorization") authorization: String,
        @Body request: UpdateTransactionRequest
    ): Response<ApiResponse<Transaction>>

    @DELETE("api/transactions/delete.php")
    suspend fun deleteTransaction(
        @Header("Authorization") authorization: String,
        @Query("id") id: Long
    ): Response<ApiResponse<Any>>

    @GET("api/budgets/list.php")
    suspend fun getBudgets(
        @Header("Authorization") authorization: String,
        @Query("month") month: Int,
        @Query("year") year: Int
    ): Response<ApiResponse<BudgetListData>>

    @POST("api/budgets/create.php")
    suspend fun createBudget(
        @Header("Authorization") authorization: String,
        @Body request: CreateBudgetRequest
    ): Response<ApiResponse<Budget>>

    @GET("api/budgets/detail.php")
    suspend fun getBudgetDetail(
        @Header("Authorization") authorization: String,
        @Query("id") id: Long
    ): Response<ApiResponse<Budget>>

    @PUT("api/budgets/update.php")
    suspend fun updateBudget(
        @Header("Authorization") authorization: String,
        @Body request: UpdateBudgetRequest
    ): Response<ApiResponse<Budget>>

    @DELETE("api/budgets/delete.php")
    suspend fun deleteBudget(
        @Header("Authorization") authorization: String,
        @Query("id") id: Long
    ): Response<ApiResponse<Any>>

    @GET("api/plans/list.php")
    suspend fun getPlans(
        @Header("Authorization") authorization: String,
        @Query("year") year: Int? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<FinancialPlan>>>

    @POST("api/plans/create.php")
    suspend fun createPlan(
        @Header("Authorization") authorization: String,
        @Body request: CreatePlanRequest
    ): Response<ApiResponse<FinancialPlan>>

    @GET("api/plans/detail.php")
    suspend fun getPlanDetail(
        @Header("Authorization") authorization: String,
        @Query("id") id: Long
    ): Response<ApiResponse<PlanDetailData>>

    @PUT("api/plans/adjust.php")
    suspend fun adjustPlan(
        @Header("Authorization") authorization: String,
        @Body request: AdjustPlanRequest
    ): Response<ApiResponse<Any>>

    @GET("api/goals/list.php")
    suspend fun getGoals(
        @Header("Authorization") authorization: String,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<Goal>>>

    @POST("api/goals/create.php")
    suspend fun createGoal(
        @Header("Authorization") authorization: String,
        @Body request: CreateGoalRequest
    ): Response<ApiResponse<Goal>>

    @GET("api/goals/detail.php")
    suspend fun getGoalDetail(
        @Header("Authorization") authorization: String,
        @Query("id") id: Long
    ): Response<ApiResponse<Goal>>

    @GET("api/goals/contributions.php")
    suspend fun getGoalContributions(
        @Header("Authorization") authorization: String,
        @Query("goal_id") goalId: Long
    ): Response<ApiResponse<List<GoalContribution>>>

    @POST("api/goals/add-contribution.php")
    suspend fun addGoalContribution(
        @Header("Authorization") authorization: String,
        @Body request: AddContributionRequest
    ): Response<ApiResponse<Any>>

    @PUT("api/goals/update.php")
    suspend fun updateGoal(
        @Header("Authorization") authorization: String,
        @Body request: UpdateGoalRequest
    ): Response<ApiResponse<Goal>>

    @DELETE("api/goals/delete.php")
    suspend fun deleteGoal(
        @Header("Authorization") authorization: String,
        @Query("id") id: Long
    ): Response<ApiResponse<Any>>

    @GET("api/statistics/category.php")
    suspend fun getCategoryStatistics(
        @Header("Authorization") authorization: String,
        @Query("month") month: Int,
        @Query("year") year: Int,
        @Query("type") type: String = "expense"
    ): Response<ApiResponse<CategoryStatisticData>>

    @GET("api/statistics/monthly.php")
    suspend fun getMonthlyStatistics(
        @Header("Authorization") authorization: String,
        @Query("year") year: Int
    ): Response<ApiResponse<MonthlyStatisticData>>

    @GET("api/notifications/list.php")
    suspend fun getNotifications(
        @Header("Authorization") authorization: String
    ): Response<ApiResponse<List<AppNotification>>>

    @PUT("api/notifications/read.php")
    suspend fun readNotification(
        @Header("Authorization") authorization: String,
        @Body request: ReadNotificationRequest
    ): Response<ApiResponse<Any>>

    @PUT("api/notifications/read-all.php")
    suspend fun readAllNotifications(
        @Header("Authorization") authorization: String
    ): Response<ApiResponse<Any>>

    @POST("api/devices/register-token.php")
    suspend fun registerDeviceToken(
        @Header("Authorization") authorization: String,
        @Body request: RegisterDeviceTokenRequest
    ): Response<ApiResponse<Any>>

    @POST("api/devices/remove-token.php")
    suspend fun removeDeviceToken(
        @Header("Authorization") authorization: String,
        @Body request: RegisterDeviceTokenRequest
    ): Response<ApiResponse<Any>>

    @GET("api/wallet/balance.php")
    suspend fun getWalletBalance(
        @Header("Authorization") authorization: String
    ): Response<ApiResponse<WalletBalanceData>>

    @POST("api/wallet/deposit.php")
    suspend fun deposit(
        @Header("Authorization") authorization: String,
        @Body request: DepositRequest
    ): Response<ApiResponse<DepositData>>

    @POST("api/wallet/transfer.php")
    suspend fun transfer(
        @Header("Authorization") authorization: String,
        @Body request: TransferRequest
    ): Response<ApiResponse<TransferData>>

    @GET("api/wallet/recipient.php")
    suspend fun getRecipient(
        @Header("Authorization") authorization: String,
        @Query("account_number") accountNumber: String
    ): Response<ApiResponse<RecipientData>>

    // ============================================================
// BANK ACCOUNTS
// ============================================================

    @GET("api/bank-accounts/list.php")
    suspend fun getBankAccounts(
        @Header("Authorization") authorization: String
    ): Response<ApiResponse<List<BankAccount>>>


    @POST("api/bank-accounts/create.php")
    suspend fun createBankAccount(
        @Header("Authorization") authorization: String,
        @Body request: CreateBankAccountRequest
    ): Response<ApiResponse<BankAccount>>


    @POST("api/bank-accounts/set-default.php")
    suspend fun setDefaultBankAccount(
        @Header("Authorization") authorization: String,
        @Body request: BankAccountActionRequest
    ): Response<ApiResponse<BankAccountActionData>>

    @POST("api/bank-accounts/delete.php")
    suspend fun deleteBankAccount(
        @Header("Authorization") authorization: String,
        @Body request: BankAccountActionRequest
    ): Response<ApiResponse<BankAccountActionData>>

    // ============================================================
// QR PAYMENT
// ============================================================

    /*
    |--------------------------------------------------------------------------
    | GỢI Ý DANH MỤC THEO CỬA HÀNG
    |--------------------------------------------------------------------------
    */

    @GET("api/payments/merchant-suggestion.php")
    suspend fun getMerchantCategorySuggestion(
        @Header("Authorization") authorization: String,
        @Query("merchant_name") merchantName: String
    ): Response<ApiResponse<MerchantCategorySuggestion>>


    /*
    |--------------------------------------------------------------------------
    | THANH TOÁN QR
    |--------------------------------------------------------------------------
    */

    @POST("api/payments/qr-pay.php")
    suspend fun payByQr(
        @Header("Authorization") authorization: String,
        @Body request: QrPaymentRequest
    ): Response<ApiResponse<QrPaymentResult>>

    @POST("api/auth/forgot-password.php")
    suspend fun forgotPassword(
        @Body request: Map<String, String>
    ): Response<ApiResponse<Any>>


}