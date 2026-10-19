package com.example.personalfinance.ui.main

import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SwapHoriz

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.personalfinance.navigation.Routes

import com.example.personalfinance.ui.dashboard.DashboardScreen

import com.example.personalfinance.ui.goal.GoalScreen

import com.example.personalfinance.ui.plan.AddPlanScreen
import com.example.personalfinance.ui.plan.AdjustPlanScreen
import com.example.personalfinance.ui.plan.PlanDetailScreen
import com.example.personalfinance.ui.plan.PlanScreen

import com.example.personalfinance.ui.profile.ChangePasswordScreen
import com.example.personalfinance.ui.profile.ProfileScreen
import com.example.personalfinance.ui.profile.SecurityScreen
import com.example.personalfinance.ui.profile.AboutScreen
import com.example.personalfinance.ui.statistics.StatisticsScreen

import com.example.personalfinance.ui.theme.BrandPurple

import com.example.personalfinance.ui.transaction.TransactionScreen
import androidx.compose.ui.unit.sp
import com.example.personalfinance.viewmodel.AuthViewModel


/*
|--------------------------------------------------------------------------
| PROFILE CHILD ROUTES
|--------------------------------------------------------------------------
*/

private const val SECURITY_ROUTE =
    "security"

private const val CHANGE_PASSWORD_ROUTE =
    "change_password"

private const val ABOUT_ROUTE = "about"
@Composable
fun MainScreen(

    onLogout: () -> Unit,

    onOpenBudget: () -> Unit,

    onOpenNotifications: () -> Unit,

    onOpenDeposit: () -> Unit,

    onOpenTransfer: () -> Unit,

    onOpenQrScanner: () -> Unit,

    onOpenBankAccounts: () -> Unit,

    onOpenAddTransaction: () -> Unit,

    onOpenEditTransaction: (Long) -> Unit,

    onOpenAddGoal: () -> Unit,

    onOpenGoalDetail: (Long) -> Unit,

    authViewModel: AuthViewModel = viewModel()

) {

    /*
    |--------------------------------------------------------------------------
    | NAV CONTROLLER
    |--------------------------------------------------------------------------
    */

    val navController =
        rememberNavController()


    val backStackEntry by
    navController
        .currentBackStackEntryAsState()


    val currentRoute =
        backStackEntry
            ?.destination
            ?.route


    /*
    |--------------------------------------------------------------------------
    | BOTTOM NAVIGATION
    |--------------------------------------------------------------------------
    */

    fun navigateBottom(
        route: String
    ) {

        navController.navigate(
            route
        ) {

            popUpTo(
                navController
                    .graph
                    .findStartDestination()
                    .id
            ) {

                saveState =
                    true
            }


            launchSingleTop =
                true


            restoreState =
                true
        }
    }


    /*
    |--------------------------------------------------------------------------
    | BOTTOM BAR
    |--------------------------------------------------------------------------
    */

    val showBottomBar =

        currentRoute == null ||

                currentRoute ==
                Routes.DASHBOARD ||

                currentRoute ==
                Routes.TRANSACTIONS ||

                currentRoute ==
                Routes.PLANS ||

                currentRoute ==
                Routes.PROFILE


    /*
    |--------------------------------------------------------------------------
    | SCAFFOLD
    |--------------------------------------------------------------------------
    */

    Scaffold(

        containerColor =
            MaterialTheme
                .colorScheme
                .background,

        bottomBar = {

            if (
                showBottomBar
            ) {

                TichPhuBottomBar(

                    currentRoute =
                        currentRoute,


                    onHomeClick = {

                        navigateBottom(
                            Routes.DASHBOARD
                        )
                    },


                    onTransactionClick = {

                        navigateBottom(
                            Routes.TRANSACTIONS
                        )
                    },


                    onQrClick =
                        onOpenQrScanner,


                    onPlanClick = {

                        navigateBottom(
                            Routes.PLANS
                        )
                    },


                    onProfileClick = {

                        navigateBottom(
                            Routes.PROFILE
                        )
                    }
                )
            }
        }

    ) {
            innerPadding ->


        /*
        |--------------------------------------------------------------------------
        | NAV HOST
        |--------------------------------------------------------------------------
        */

        NavHost(

            navController =
                navController,

            startDestination =
                Routes.DASHBOARD,

            modifier =
                Modifier.padding(
                    innerPadding
                )

        ) {


            /*
            |--------------------------------------------------------------------------
            | DASHBOARD
            |--------------------------------------------------------------------------
            */

            composable(
                Routes.DASHBOARD
            ) {

                DashboardScreen(

                    onLogout = {

                        authViewModel
                            .logout()

                        onLogout()
                    },


                    onTransactionsClick = {

                        navigateBottom(
                            Routes.TRANSACTIONS
                        )
                    },


                    onDepositClick =
                        onOpenDeposit,


                    onTransferClick =
                        onOpenTransfer,


                    onQrScanClick =
                        onOpenQrScanner,


                    onBudgetClick =
                        onOpenBudget,


                    onPlanClick = {

                        navigateBottom(
                            Routes.PLANS
                        )
                    },


                    onGoalClick = {

                        navController.navigate(
                            Routes.GOALS
                        )
                    },


                    onStatisticsClick = {

                        navController.navigate(
                            Routes.STATISTICS
                        )
                    },


                    onNotificationClick =
                        onOpenNotifications,


                    onProfileClick = {

                        navigateBottom(
                            Routes.PROFILE
                        )
                    },


                    onSearchClick = {

                        navigateBottom(
                            Routes.TRANSACTIONS
                        )
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | TRANSACTIONS
            |--------------------------------------------------------------------------
            */

            composable(
                Routes.TRANSACTIONS
            ) {

                TransactionScreen(

                    onBack = {

                        navigateBottom(
                            Routes.DASHBOARD
                        )
                    },


                    onAddClick =
                        onOpenAddTransaction,


                    onEditClick =
                        onOpenEditTransaction
                )
            }


            /*
            |--------------------------------------------------------------------------
            | PLANS
            |--------------------------------------------------------------------------
            */

            composable(
                Routes.PLANS
            ) {

                PlanScreen(

                    onBack = {

                        navigateBottom(
                            Routes.DASHBOARD
                        )
                    },


                    onAddClick = {

                        navController.navigate(
                            Routes.ADD_PLAN
                        )
                    },


                    onDetailClick = {
                            id ->

                        navController.navigate(
                            Routes.planDetail(
                                id
                            )
                        )
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | ADD PLAN
            |--------------------------------------------------------------------------
            */

            composable(
                Routes.ADD_PLAN
            ) {

                AddPlanScreen(

                    onBack = {

                        navController
                            .popBackStack()
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | PLAN DETAIL
            |--------------------------------------------------------------------------
            */

            composable(

                route =
                    Routes.PLAN_DETAIL,

                arguments =
                    listOf(

                        navArgument(
                            "id"
                        ) {

                            type =
                                NavType.LongType
                        }
                    )

            ) {
                    backStackEntry ->


                val id =
                    backStackEntry
                        .arguments
                        ?.getLong(
                            "id"
                        )
                        ?: return@composable


                PlanDetailScreen(

                    planId =
                        id,


                    onBack = {

                        navController
                            .popBackStack()
                    },


                    onAdjustClick = {
                            planId ->

                        navController.navigate(
                            Routes.adjustPlan(
                                planId
                            )
                        )
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | ADJUST PLAN
            |--------------------------------------------------------------------------
            */

            composable(

                route =
                    Routes.ADJUST_PLAN,

                arguments =
                    listOf(

                        navArgument(
                            "id"
                        ) {

                            type =
                                NavType.LongType
                        }
                    )

            ) {
                    backStackEntry ->


                val id =
                    backStackEntry
                        .arguments
                        ?.getLong(
                            "id"
                        )
                        ?: return@composable


                AdjustPlanScreen(

                    planId =
                        id,

                    onBack = {

                        navController
                            .popBackStack()
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | GOALS
            |--------------------------------------------------------------------------
            */

            composable(
                Routes.GOALS
            ) {

                GoalScreen(

                    onBack = {

                        navController
                            .popBackStack()
                    },


                    onAddClick =
                        onOpenAddGoal,


                    onDetailClick =
                        onOpenGoalDetail
                )
            }


            /*
            |--------------------------------------------------------------------------
            | STATISTICS
            |--------------------------------------------------------------------------
            */

            composable(
                Routes.STATISTICS
            ) {

                StatisticsScreen(

                    onBack = {

                        navController
                            .popBackStack()
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | PROFILE
            |--------------------------------------------------------------------------
            */

            composable(
                Routes.PROFILE
            ) {

                ProfileScreen(

                    onBack = {
                        navigateBottom(
                            Routes.DASHBOARD
                        )
                    },

                    onLogout = {
                        authViewModel.logout()
                        onLogout()
                    },

                    onBankAccountClick =
                        onOpenBankAccounts,

                    onNotificationClick =
                        onOpenNotifications,

                    onSecurityClick = {
                        navController.navigate(
                            SECURITY_ROUTE
                        )
                    },

                    onAboutClick = {
                        navController.navigate(
                            ABOUT_ROUTE
                        )
                    },

                    authViewModel =
                        authViewModel
                )
            }


            /*
            |--------------------------------------------------------------------------
            | SECURITY
            |--------------------------------------------------------------------------
            */

            composable(
                SECURITY_ROUTE
            ) {

                SecurityScreen(

                    /*
                    |--------------------------------------------------------------------------
                    | SECURITY -> PROFILE
                    |--------------------------------------------------------------------------
                    */

                    onBack = {

                        navController
                            .popBackStack()
                    },


                    /*
                    |--------------------------------------------------------------------------
                    | SECURITY -> CHANGE PASSWORD
                    |--------------------------------------------------------------------------
                    */

                    onChangePasswordClick = {

                        navController.navigate(
                            CHANGE_PASSWORD_ROUTE
                        )
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | CHANGE PASSWORD
            |--------------------------------------------------------------------------
            */

            composable(
                CHANGE_PASSWORD_ROUTE
            ) {

                ChangePasswordScreen(

                    /*
                    |--------------------------------------------------------------------------
                    | CHANGE PASSWORD -> SECURITY
                    |--------------------------------------------------------------------------
                    */

                    onBack = {

                        navController
                            .popBackStack()
                    },


                    authViewModel =
                        authViewModel
                )
            }

            /*
|--------------------------------------------------------------------------
| ABOUT
|--------------------------------------------------------------------------
*/

            composable(
                ABOUT_ROUTE
            ) {

                AboutScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| BOTTOM BAR
|--------------------------------------------------------------------------
|
| Trang chủ | Giao dịch | QR | Kế hoạch | Cá nhân
|
*/

@Composable
private fun TichPhuBottomBar(
    currentRoute: String?,
    onHomeClick: () -> Unit,
    onTransactionClick: () -> Unit,
    onQrClick: () -> Unit,
    onPlanClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .navigationBarsPadding()
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 5.dp,
            shadowElevation = 12.dp
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp)
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // TRANG CHỦ
                BottomItem(
                    title = "Trang chủ",
                    icon = Icons.Default.Home,
                    selected = currentRoute == Routes.DASHBOARD,
                    onClick = onHomeClick,
                    modifier = Modifier.weight(1f)
                )

                // GIAO DỊCH
                BottomItem(
                    title = "Giao dịch",
                    icon = Icons.Default.SwapHoriz,
                    selected = currentRoute == Routes.TRANSACTIONS,
                    onClick = onTransactionClick,
                    modifier = Modifier.weight(1f)
                )

                // KHOẢNG TRỐNG CHO QR
                Spacer(
                    modifier = Modifier.weight(1f)
                )

                // KẾ HOẠCH
                BottomItem(
                    title = "Kế hoạch",
                    icon = Icons.Default.EventNote,
                    selected = currentRoute == Routes.PLANS,
                    onClick = onPlanClick,
                    modifier = Modifier.weight(1f)
                )

                // CÁ NHÂN
                BottomItem(
                    title = "Cá nhân",
                    icon = Icons.Default.Person,
                    selected = currentRoute == Routes.PROFILE,
                    onClick = onProfileClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // VÒNG TRÒN BAO QUANH NÚT QR
        Surface(
            modifier = Modifier
                .size(72.dp)
                .align(Alignment.TopCenter),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.background
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {

                FloatingActionButton(
                    onClick = onQrClick,
                    modifier = Modifier.size(60.dp),
                    shape = CircleShape,
                    containerColor = BrandPurple,
                    contentColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = "Quét QR",
                        modifier = Modifier.size(27.dp)
                    )
                }
            }
        }

        // CHỮ QUÉT QR
        Text(
            text = "",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 3.dp),
            fontSize = 10.sp,
            color = BrandPurple,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}


/*
|--------------------------------------------------------------------------
| BOTTOM ITEM
|--------------------------------------------------------------------------
*/

@Composable
private fun BottomItem(
    title: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedColor = BrandPurple
    val normalColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .height(62.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (selected) {
                selectedColor
            } else {
                normalColor
            },
            modifier = Modifier.size(20.dp)
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = title,
            color = if (selected) {
                selectedColor
            } else {
                normalColor
            },
            fontSize = 7.sp,
            fontWeight = if (selected) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            },
            maxLines = 1,
            softWrap = false,
            textAlign = TextAlign.Center,
            lineHeight = 8.sp
        )
    }
}

