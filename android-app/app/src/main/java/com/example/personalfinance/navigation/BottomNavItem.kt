package com.example.personalfinance.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Savings
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = Routes.DASHBOARD,
        title = "Trang chủ",
        icon = Icons.Default.Home
    ),

    BottomNavItem(
        route = Routes.TRANSACTIONS,
        title = "Giao dịch",
        icon = Icons.Default.ReceiptLong
    ),

    BottomNavItem(
        route = Routes.STATISTICS,
        title = "Thống kê",
        icon = Icons.Default.BarChart
    ),

    BottomNavItem(
        route = Routes.GOALS,
        title = "Mục tiêu",
        icon = Icons.Default.Savings
    ),

    BottomNavItem(
        route = Routes.PROFILE,
        title = "Cá nhân",
        icon = Icons.Default.AccountCircle
    )
)