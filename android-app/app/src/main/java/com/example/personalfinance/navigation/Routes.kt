package com.example.personalfinance.navigation

object Routes {

    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD =
        "forgot_password"

    const val DASHBOARD = "dashboard"

    const val TRANSACTIONS =
        "transactions"

    const val ADD_TRANSACTION =
        "add_transaction"

    const val EDIT_TRANSACTION =
        "edit_transaction/{id}"

    const val PROFILE = "profile"

    const val MAIN = "main"

    const val DEPOSIT = "deposit"
    const val TRANSFER = "transfer"

    const val BANK_ACCOUNTS = "bank_accounts"

    const val QR_SCANNER = "qr_scanner"

    const val QR_PAYMENT_CONFIRM =
        "qr_payment_confirm/{qrRaw}"

    fun editTransaction(
        id: Long
    ): String {

        return "edit_transaction/$id"
    }

    const val BUDGET = "budget"

    const val ADD_BUDGET =
        "add_budget"

    const val EDIT_BUDGET =
        "edit_budget/{id}"

    fun editBudget(
        id: Long
    ): String {

        return "edit_budget/$id"
    }

    const val PLANS = "plans"

    const val ADD_PLAN =
        "add_plan"

    const val PLAN_DETAIL =
        "plan_detail/{id}"

    fun planDetail(
        id: Long
    ): String {

        return "plan_detail/$id"
    }

    const val ADJUST_PLAN =
        "adjust_plan/{id}"

    fun adjustPlan(
        id: Long
    ): String {

        return "adjust_plan/$id"
    }

    const val GOALS = "goals"

    const val ADD_GOAL =
        "add_goal"

    const val GOAL_DETAIL =
        "goal_detail/{id}"

    fun goalDetail(
        id: Long
    ): String {

        return "goal_detail/$id"
    }

    const val ADD_CONTRIBUTION =
        "add_contribution/{id}"

    fun addContribution(
        id: Long
    ): String {

        return "add_contribution/$id"
    }

    const val EDIT_GOAL =
        "edit_goal/{id}"

    fun editGoal(
        id: Long
    ): String {

        return "edit_goal/$id"
    }

    const val STATISTICS =
        "statistics"

    const val NOTIFICATIONS =
        "notifications"
}