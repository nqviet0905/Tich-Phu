package com.example.personalfinance.navigation

import android.net.Uri

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.personalfinance.data.model.QrPaymentParser

import com.example.personalfinance.ui.auth.ForgotPasswordScreen
import com.example.personalfinance.ui.auth.LoginScreen
import com.example.personalfinance.ui.auth.RegisterScreen
import com.example.personalfinance.ui.auth.WelcomeScreen

import com.example.personalfinance.ui.bank.BankAccountScreen

import com.example.personalfinance.ui.budget.AddBudgetScreen
import com.example.personalfinance.ui.budget.BudgetScreen
import com.example.personalfinance.ui.budget.EditBudgetScreen

import com.example.personalfinance.ui.goal.AddContributionScreen
import com.example.personalfinance.ui.goal.AddGoalScreen
import com.example.personalfinance.ui.goal.EditGoalScreen
import com.example.personalfinance.ui.goal.GoalDetailScreen

import com.example.personalfinance.ui.main.MainScreen

import com.example.personalfinance.ui.notification.NotificationScreen

import com.example.personalfinance.ui.payment.QrPaymentConfirmScreen
import com.example.personalfinance.ui.payment.QrScannerScreen

import com.example.personalfinance.ui.transaction.AddTransactionScreen
import com.example.personalfinance.ui.transaction.EditTransactionScreen

import com.example.personalfinance.ui.wallet.DepositScreen
import com.example.personalfinance.ui.wallet.TransferScreen

import com.example.personalfinance.viewmodel.AuthViewModel


@Composable
fun AppNavigation() {

    val navController =
        rememberNavController()

    val authViewModel: AuthViewModel =
        viewModel()

    val authState by
    authViewModel.uiState.collectAsState()


    /*
    |--------------------------------------------------------------------------
    | START DESTINATION
    |--------------------------------------------------------------------------
    */

    val startDestination =
        if (authState.isLoggedIn) {

            Routes.MAIN

        } else {

            Routes.WELCOME
        }


    NavHost(
        navController =
            navController,
        startDestination =
            startDestination
    ) {


        // =====================================================
        // AUTH
        // =====================================================


        /*
        |--------------------------------------------------------------------------
        | WELCOME
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.WELCOME
        ) {

            WelcomeScreen(

                onLoginClick = {

                    navController.navigate(
                        Routes.LOGIN
                    )
                },


                onRegisterClick = {

                    navController.navigate(
                        Routes.REGISTER
                    )
                }
            )
        }


        /*
        |--------------------------------------------------------------------------
        | LOGIN
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.LOGIN
        ) {

            LoginScreen(

                onLoginSuccess = {

                    navController.navigate(
                        Routes.MAIN
                    ) {

                        popUpTo(
                            Routes.WELCOME
                        ) {

                            inclusive =
                                true
                        }

                        launchSingleTop =
                            true
                    }
                },


                onForgotPassword = {

                    navController.navigate(
                        Routes.FORGOT_PASSWORD
                    )
                },


                onRegisterClick = {

                    navController.navigate(
                        Routes.REGISTER
                    )
                },


                onBack = {

                    navController.popBackStack()
                },


                authViewModel =
                    authViewModel
            )
        }


        /*
        |--------------------------------------------------------------------------
        | REGISTER
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.REGISTER
        ) {

            RegisterScreen(

                onBack = {

                    navController.popBackStack()
                },


                onRegisterSuccess = {

                    navController.navigate(
                        Routes.MAIN
                    ) {

                        popUpTo(
                            Routes.WELCOME
                        ) {

                            inclusive =
                                true
                        }

                        launchSingleTop =
                            true
                    }
                },


                authViewModel =
                    authViewModel
            )
        }


        /*
        |--------------------------------------------------------------------------
        | FORGOT PASSWORD
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.FORGOT_PASSWORD
        ) {

            ForgotPasswordScreen(

                onBack = {

                    navController.popBackStack()
                },


                authViewModel =
                    authViewModel
            )
        }


        // =====================================================
        // MAIN
        // =====================================================

        /*
        |--------------------------------------------------------------------------
        | MAIN SCREEN
        |--------------------------------------------------------------------------
        |
        | MainScreen tự quản lý:
        |
        | - Dashboard
        | - Transactions
        | - Plans
        | - Goal list
        | - Statistics
        | - Profile
        |
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.MAIN
        ) {

            MainScreen(

                /*
                |--------------------------------------------------------------------------
                | LOGOUT
                |--------------------------------------------------------------------------
                */

                onLogout = {

                    authViewModel.logout()


                    navController.navigate(
                        Routes.WELCOME
                    ) {

                        popUpTo(
                            navController.graph.id
                        ) {

                            inclusive =
                                true
                        }

                        launchSingleTop =
                            true
                    }
                },


                /*
                |--------------------------------------------------------------------------
                | BUDGET
                |--------------------------------------------------------------------------
                */

                onOpenBudget = {

                    navController.navigate(
                        Routes.BUDGET
                    )
                },


                /*
                |--------------------------------------------------------------------------
                | NOTIFICATION
                |--------------------------------------------------------------------------
                */

                onOpenNotifications = {

                    navController.navigate(
                        Routes.NOTIFICATIONS
                    )
                },


                /*
                |--------------------------------------------------------------------------
                | DEPOSIT
                |--------------------------------------------------------------------------
                */

                onOpenDeposit = {

                    navController.navigate(
                        Routes.DEPOSIT
                    )
                },


                /*
                |--------------------------------------------------------------------------
                | TRANSFER
                |--------------------------------------------------------------------------
                */

                onOpenTransfer = {

                    navController.navigate(
                        Routes.TRANSFER
                    )
                },


                /*
                |--------------------------------------------------------------------------
                | QR
                |--------------------------------------------------------------------------
                */

                onOpenQrScanner = {

                    navController.navigate(
                        Routes.QR_SCANNER
                    )
                },


                /*
                |--------------------------------------------------------------------------
                | BANK
                |--------------------------------------------------------------------------
                */

                onOpenBankAccounts = {

                    navController.navigate(
                        Routes.BANK_ACCOUNTS
                    )
                },


                /*
                |--------------------------------------------------------------------------
                | ADD TRANSACTION
                |--------------------------------------------------------------------------
                */

                onOpenAddTransaction = {

                    navController.navigate(
                        Routes.ADD_TRANSACTION
                    )
                },


                /*
                |--------------------------------------------------------------------------
                | EDIT TRANSACTION
                |--------------------------------------------------------------------------
                */

                onOpenEditTransaction = { id ->

                    navController.navigate(
                        Routes.editTransaction(
                            id
                        )
                    )
                },


                /*
                |--------------------------------------------------------------------------
                | ADD GOAL
                |--------------------------------------------------------------------------
                */

                onOpenAddGoal = {

                    navController.navigate(
                        Routes.ADD_GOAL
                    )
                },


                /*
                |--------------------------------------------------------------------------
                | GOAL DETAIL
                |--------------------------------------------------------------------------
                */

                onOpenGoalDetail = { id ->

                    navController.navigate(
                        Routes.goalDetail(
                            id
                        )
                    )
                },


                authViewModel =
                    authViewModel
            )
        }


        // =====================================================
        // QR
        // =====================================================


        /*
        |--------------------------------------------------------------------------
        | QR SCANNER
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.QR_SCANNER
        ) {

            QrScannerScreen(

                onBack = {

                    navController.popBackStack()
                },


                onQrScanned = { qrData ->

                    val encodedQr =
                        Uri.encode(
                            qrData.rawValue
                        )


                    navController.navigate(
                        "qr_payment_confirm/$encodedQr"
                    )
                }
            )
        }


        /*
        |--------------------------------------------------------------------------
        | QR CONFIRM
        |--------------------------------------------------------------------------
        */

        composable(

            route =
                Routes.QR_PAYMENT_CONFIRM,

            arguments =
                listOf(

                    navArgument(
                        "qrRaw"
                    ) {

                        type =
                            NavType.StringType
                    }
                )

        ) { backStackEntry ->


            val encodedQr =
                backStackEntry
                    .arguments
                    ?.getString(
                        "qrRaw"
                    )
                    .orEmpty()


            val rawQr =
                Uri.decode(
                    encodedQr
                )


            val qrData =
                QrPaymentParser.parse(
                    rawQr
                )


            if (qrData != null) {

                QrPaymentConfirmScreen(

                    qrData =
                        qrData,


                    onBack = {

                        navController.popBackStack()
                    },


                    onPaymentSuccess = {

                        navController.navigate(
                            Routes.MAIN
                        ) {

                            popUpTo(
                                Routes.MAIN
                            ) {

                                inclusive =
                                    false
                            }

                            launchSingleTop =
                                true
                        }
                    }
                )

            } else {

                LaunchedEffect(Unit) {

                    navController.popBackStack()
                }
            }
        }


        // =====================================================
        // BANK ACCOUNT
        // =====================================================


        composable(
            route =
                Routes.BANK_ACCOUNTS
        ) {

            BankAccountScreen(

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // TRANSACTION CHILD SCREENS
        // =====================================================


        /*
        |--------------------------------------------------------------------------
        | ADD TRANSACTION
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.ADD_TRANSACTION
        ) {

            AddTransactionScreen(

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        /*
        |--------------------------------------------------------------------------
        | EDIT TRANSACTION
        |--------------------------------------------------------------------------
        */

        composable(

            route =
                Routes.EDIT_TRANSACTION,

            arguments =
                listOf(

                    navArgument(
                        "id"
                    ) {

                        type =
                            NavType.LongType
                    }
                )

        ) { backStackEntry ->


            val id =
                backStackEntry
                    .arguments
                    ?.getLong(
                        "id"
                    )
                    ?: return@composable


            EditTransactionScreen(

                transactionId =
                    id,


                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // BUDGET
        // =====================================================


        /*
        |--------------------------------------------------------------------------
        | BUDGET LIST
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.BUDGET
        ) {

            BudgetScreen(

                onBack = {

                    navController.popBackStack()
                },


                onAddClick = {

                    navController.navigate(
                        Routes.ADD_BUDGET
                    )
                },


                onEditClick = { id ->

                    navController.navigate(
                        Routes.editBudget(
                            id
                        )
                    )
                }
            )
        }


        /*
        |--------------------------------------------------------------------------
        | ADD BUDGET
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.ADD_BUDGET
        ) {

            AddBudgetScreen(

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        /*
        |--------------------------------------------------------------------------
        | EDIT BUDGET
        |--------------------------------------------------------------------------
        */

        composable(

            route =
                Routes.EDIT_BUDGET,

            arguments =
                listOf(

                    navArgument(
                        "id"
                    ) {

                        type =
                            NavType.LongType
                    }
                )

        ) { backStackEntry ->


            val id =
                backStackEntry
                    .arguments
                    ?.getLong(
                        "id"
                    )
                    ?: return@composable


            EditBudgetScreen(

                budgetId =
                    id,


                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // GOAL CHILD SCREENS
        // =====================================================


        /*
        |--------------------------------------------------------------------------
        | ADD GOAL
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.ADD_GOAL
        ) {

            AddGoalScreen(

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        /*
        |--------------------------------------------------------------------------
        | GOAL DETAIL
        |--------------------------------------------------------------------------
        */

        composable(

            route =
                Routes.GOAL_DETAIL,

            arguments =
                listOf(

                    navArgument(
                        "id"
                    ) {

                        type =
                            NavType.LongType
                    }
                )

        ) { backStackEntry ->


            val id =
                backStackEntry
                    .arguments
                    ?.getLong(
                        "id"
                    )
                    ?: return@composable


            GoalDetailScreen(

                goalId =
                    id,


                onBack = {

                    navController.popBackStack()
                },


                onAddContributionClick = { goalId ->

                    navController.navigate(
                        Routes.addContribution(
                            goalId
                        )
                    )
                },


                onEditClick = { goalId ->

                    navController.navigate(
                        Routes.editGoal(
                            goalId
                        )
                    )
                }
            )
        }


        /*
        |--------------------------------------------------------------------------
        | ADD CONTRIBUTION
        |--------------------------------------------------------------------------
        */

        composable(

            route =
                Routes.ADD_CONTRIBUTION,

            arguments =
                listOf(

                    navArgument(
                        "id"
                    ) {

                        type =
                            NavType.LongType
                    }
                )

        ) { backStackEntry ->


            val id =
                backStackEntry
                    .arguments
                    ?.getLong(
                        "id"
                    )
                    ?: return@composable


            AddContributionScreen(

                goalId =
                    id,


                onBack = {

                    navController.popBackStack()
                }
            )
        }


        /*
        |--------------------------------------------------------------------------
        | EDIT GOAL
        |--------------------------------------------------------------------------
        */

        composable(

            route =
                Routes.EDIT_GOAL,

            arguments =
                listOf(

                    navArgument(
                        "id"
                    ) {

                        type =
                            NavType.LongType
                    }
                )

        ) { backStackEntry ->


            val id =
                backStackEntry
                    .arguments
                    ?.getLong(
                        "id"
                    )
                    ?: return@composable


            EditGoalScreen(

                goalId =
                    id,


                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // NOTIFICATION
        // =====================================================


        composable(
            route =
                Routes.NOTIFICATIONS
        ) {

            NotificationScreen(

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        // =====================================================
        // WALLET
        // =====================================================


        /*
        |--------------------------------------------------------------------------
        | DEPOSIT
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.DEPOSIT
        ) {

            DepositScreen(

                onBack = {

                    navController.popBackStack()
                }
            )
        }


        /*
        |--------------------------------------------------------------------------
        | TRANSFER
        |--------------------------------------------------------------------------
        */

        composable(
            route =
                Routes.TRANSFER
        ) {

            TransferScreen(

                onBack = {

                    navController.popBackStack()
                }
            )
        }
    }
}