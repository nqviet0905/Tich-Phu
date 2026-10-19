package com.example.personalfinance.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.ui.theme.BrandGold
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.ui.theme.WarningOrange
import com.example.personalfinance.viewmodel.BudgetViewModel

import java.text.NumberFormat
import java.util.Locale


@Composable
fun BudgetScreen(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    budgetViewModel: BudgetViewModel = viewModel()
) {

    val state by
    budgetViewModel.uiState.collectAsState()


    val formatter =
        remember {
            NumberFormat.getNumberInstance(
                Locale(
                    "vi",
                    "VN"
                )
            )
        }


    var budgetIdToDelete by remember {
        mutableStateOf<Long?>(null)
    }


    /*
    |--------------------------------------------------------------------------
    | DELETE DIALOG
    |--------------------------------------------------------------------------
    */

    if (budgetIdToDelete != null) {

        AlertDialog(
            onDismissRequest = {

                budgetIdToDelete =
                    null
            },
            shape =
                RoundedCornerShape(
                    24.dp
                ),
            title = {

                Text(
                    text =
                        "Xóa ngân sách?",
                    fontWeight =
                        FontWeight.ExtraBold
                )
            },
            text = {

                Text(
                    text =
                        "Ngân sách này sẽ bị xóa khỏi danh sách. Các giao dịch đã ghi nhận vẫn được giữ nguyên."
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        val id =
                            budgetIdToDelete

                        if (id != null) {

                            budgetViewModel
                                .deleteBudget(
                                    id
                                )
                        }

                        budgetIdToDelete =
                            null
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        ),
                    shape =
                        RoundedCornerShape(
                            14.dp
                        )
                ) {

                    Text(
                        text =
                            "Xóa ngân sách",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {

                        budgetIdToDelete =
                            null
                    }
                ) {

                    Text(
                        "Không"
                    )
                }
            }
        )
    }


    /*
    |--------------------------------------------------------------------------
    | ROOT
    |--------------------------------------------------------------------------
    */

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
    ) {


        /*
        |--------------------------------------------------------------------------
        | HEADER
        |--------------------------------------------------------------------------
        */

        BudgetHeader(
            onBack =
                onBack,
            onAddClick =
                onAddClick
        )


        /*
        |--------------------------------------------------------------------------
        | CONTENT
        |--------------------------------------------------------------------------
        */

        when {


            /*
            |--------------------------------------------------------------------------
            | LOADING
            |--------------------------------------------------------------------------
            */

            state.isLoading -> {

                Box(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | ERROR
            |--------------------------------------------------------------------------
            */

            state.error != null -> {

                BudgetErrorState(
                    message =
                        state.error
                            ?: "Đã xảy ra lỗi.",
                    onRetry = {

                        budgetViewModel
                            .loadBudgets()
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | DATA
            |--------------------------------------------------------------------------
            */

            else -> {

                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize(),
                    contentPadding =
                        PaddingValues(
                            start = 18.dp,
                            end = 18.dp,
                            top = 8.dp,
                            bottom = 110.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            14.dp
                        )
                ) {


                    /*
                    |--------------------------------------------------------------------------
                    | SUMMARY
                    |--------------------------------------------------------------------------
                    */

                    item {

                        BudgetSummaryCard(
                            totalBudget =
                                state.totalBudget,
                            totalSpent =
                                state.totalSpent,
                            totalRemaining =
                                state.totalRemaining,
                            formatter =
                                formatter
                        )
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | SECTION TITLE
                    |--------------------------------------------------------------------------
                    */

                    item {

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text =
                                    "Ngân sách của bạn",
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium,
                                fontWeight =
                                    FontWeight.ExtraBold,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onBackground
                            )


                            Text(
                                text =
                                    "${state.budgets.size} ngân sách",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )
                        }
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | EMPTY
                    |--------------------------------------------------------------------------
                    */

                    if (
                        state.budgets.isEmpty()
                    ) {

                        item {

                            BudgetEmptyState(
                                onAddClick =
                                    onAddClick
                            )
                        }

                    } else {


                        /*
                        |--------------------------------------------------------------------------
                        | BUDGET LIST
                        |--------------------------------------------------------------------------
                        */

                        items(
                            items =
                                state.budgets,
                            key = {
                                it.id
                            }
                        ) { budget ->


                            val statusColor =
                                when (
                                    budget.status
                                ) {

                                    "normal" ->
                                        IncomeGreen

                                    "attention" ->
                                        BrandGold

                                    "warning" ->
                                        WarningOrange

                                    "exceeded" ->
                                        ExpenseRed

                                    else ->
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                }


                            val statusText =
                                when (
                                    budget.status
                                ) {

                                    "normal" ->
                                        "Bình thường"

                                    "attention" ->
                                        "Cần chú ý"

                                    "warning" ->
                                        "Cảnh báo"

                                    "exceeded" ->
                                        "Vượt ngân sách"

                                    else ->
                                        budget.status
                                }


                            val progress =
                                (
                                        budget
                                            .usage_percentage
                                            .toFloat() / 100f
                                        ).coerceIn(
                                        0f,
                                        1f
                                    )


                            BudgetItemCard(
                                categoryName =
                                    budget.category_name,
                                month =
                                    budget.month,
                                year =
                                    budget.year,
                                amount =
                                    budget.amount,
                                spentAmount =
                                    budget.spent_amount,
                                remainingAmount =
                                    budget.remaining_amount,
                                usagePercentage =
                                    budget.usage_percentage,
                                progress =
                                    progress,
                                statusText =
                                    statusText,
                                statusColor =
                                    statusColor,
                                formatter =
                                    formatter,
                                onEditClick = {

                                    onEditClick(
                                        budget.id
                                    )
                                },
                                onDeleteClick = {

                                    budgetIdToDelete =
                                        budget.id
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| HEADER
|--------------------------------------------------------------------------
*/

@Composable
private fun BudgetHeader(
    onBack: () -> Unit,
    onAddClick: () -> Unit
) {

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    start = 18.dp,
                    end = 18.dp,
                    top = 16.dp,
                    bottom = 10.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {


        /*
        |--------------------------------------------------------------------------
        | BACK
        |--------------------------------------------------------------------------
        */

        Surface(
            modifier =
                Modifier.size(
                    44.dp
                ),
            shape =
                CircleShape,
            color =
                MaterialTheme
                    .colorScheme
                    .surfaceVariant
                    .copy(
                        alpha = 0.65f
                    )
        ) {

            IconButton(
                onClick =
                    onBack
            ) {

                Icon(
                    imageVector =
                        Icons.AutoMirrored
                            .Filled
                            .ArrowBack,
                    contentDescription =
                        "Quay lại"
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | TITLE
        |--------------------------------------------------------------------------
        */

        Column(
            modifier =
                Modifier
                    .weight(
                        1f
                    )
                    .padding(
                        start = 13.dp,
                        end = 8.dp
                    )
        ) {

            Text(
                text =
                    "Ngân sách",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                fontWeight =
                    FontWeight.ExtraBold,
                color =
                    MaterialTheme
                        .colorScheme
                        .onBackground
            )


            Spacer(
                modifier =
                    Modifier.height(
                        2.dp
                    )
            )


            Text(
                text =
                    "Kiểm soát giới hạn chi tiêu",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }


        /*
        |--------------------------------------------------------------------------
        | ADD
        |--------------------------------------------------------------------------
        */

        Button(
            onClick =
                onAddClick,
            shape =
                RoundedCornerShape(
                    16.dp
                ),
            contentPadding =
                PaddingValues(
                    horizontal = 13.dp,
                    vertical = 10.dp
                )
        ) {

            Icon(
                imageVector =
                    Icons.Default.Add,
                contentDescription =
                    null,
                modifier =
                    Modifier.size(
                        18.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.size(
                        5.dp
                    )
            )


            Text(
                text =
                    "Thêm",
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| SUMMARY
|--------------------------------------------------------------------------
*/

@Composable
private fun BudgetSummaryCard(
    totalBudget: Double,
    totalSpent: Double,
    totalRemaining: Double,
    formatter: NumberFormat
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                26.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.Transparent
            )
    ) {

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        brush =
                            Brush.linearGradient(
                                colors =
                                    listOf(
                                        BrandPurpleDark,
                                        BrandPurple,
                                        Color(
                                            0xFF8058FF
                                        )
                                    )
                            ),
                        shape =
                            RoundedCornerShape(
                                26.dp
                            )
                    )
                    .padding(
                        20.dp
                    )
        ) {

            Column {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Surface(
                        modifier =
                            Modifier.size(
                                46.dp
                            ),
                        shape =
                            RoundedCornerShape(
                                15.dp
                            ),
                        color =
                            Color.White.copy(
                                alpha = 0.12f
                            )
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default
                                        .AccountBalanceWallet,
                                contentDescription =
                                    null,
                                tint =
                                    Color.White,
                                modifier =
                                    Modifier.size(
                                        25.dp
                                    )
                            )
                        }
                    }


                    Column(
                        modifier =
                            Modifier.padding(
                                start = 12.dp
                            )
                    ) {

                        Text(
                            text =
                                "Tổng ngân sách",
                            color =
                                Color.White.copy(
                                    alpha = 0.78f
                                ),
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    2.dp
                                )
                        )


                        Text(
                            text =
                                "${
                                    formatter.format(
                                        totalBudget
                                    )
                                } đ",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineSmall,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color =
                                Color.White
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            18.dp
                        )
                )


                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {

                    BudgetMiniSummary(
                        title =
                            "Đã chi",
                        value =
                            "${
                                formatter.format(
                                    totalSpent
                                )
                            } đ",
                        valueColor =
                            Color(
                                0xFFFFB4C1
                            ),
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )


                    BudgetMiniSummary(
                        title =
                            "Còn lại",
                        value =
                            "${
                                formatter.format(
                                    totalRemaining
                                )
                            } đ",
                        valueColor =
                            if (
                                totalRemaining >= 0
                            ) {

                                Color(
                                    0xFF9EF0C5
                                )

                            } else {

                                Color(
                                    0xFFFFB4C1
                                )
                            },
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| MINI SUMMARY
|--------------------------------------------------------------------------
*/

@Composable
private fun BudgetMiniSummary(
    title: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier =
            modifier,
        shape =
            RoundedCornerShape(
                17.dp
            ),
        color =
            Color.White.copy(
                alpha = 0.10f
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    13.dp
                )
        ) {

            Text(
                text =
                    title,
                color =
                    Color.White.copy(
                        alpha = 0.72f
                    ),
                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )


            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )


            Text(
                text =
                    value,
                color =
                    valueColor,
                fontWeight =
                    FontWeight.ExtraBold,
                maxLines =
                    1,
                overflow =
                    TextOverflow.Ellipsis
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| BUDGET ITEM
|--------------------------------------------------------------------------
*/

@Composable
private fun BudgetItemCard(
    categoryName: String,
    month: Int,
    year: Int,
    amount: Double,
    spentAmount: Double,
    remainingAmount: Double,
    usagePercentage: Double,
    progress: Float,
    statusText: String,
    statusColor: Color,
    formatter: NumberFormat,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    2.dp
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        17.dp
                    )
        ) {


            /*
            |--------------------------------------------------------------------------
            | HEADER
            |--------------------------------------------------------------------------
            */

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(
                            50.dp
                        ),
                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),
                    color =
                        statusColor.copy(
                            alpha = 0.11f
                        )
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default
                                    .AccountBalanceWallet,
                            contentDescription =
                                null,
                            tint =
                                statusColor,
                            modifier =
                                Modifier.size(
                                    25.dp
                                )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.size(
                            12.dp
                        )
                )


                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            categoryName,
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,
                        maxLines =
                            1,
                        overflow =
                            TextOverflow.Ellipsis
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                3.dp
                            )
                    )


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default
                                    .CalendarMonth,
                            contentDescription =
                                null,
                            modifier =
                                Modifier.size(
                                    14.dp
                                ),
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )


                        Spacer(
                            modifier =
                                Modifier.size(
                                    4.dp
                                )
                        )


                        Text(
                            text =
                                "Tháng $month/$year",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.size(
                            6.dp
                        )
                )


                BudgetStatusBadge(
                    text =
                        statusText,
                    color =
                        statusColor
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        17.dp
                    )
            )


            /*
            |--------------------------------------------------------------------------
            | MONEY
            |--------------------------------------------------------------------------
            */

            Text(
                text =
                    "Đã chi",
                style =
                    MaterialTheme
                        .typography
                        .bodySmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )


            Spacer(
                modifier =
                    Modifier.height(
                        3.dp
                    )
            )


            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.Bottom
            ) {

                Text(
                    text =
                        "${
                            formatter.format(
                                spentAmount
                            )
                        } đ",
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    fontWeight =
                        FontWeight.ExtraBold,
                    color =
                        statusColor
                )


                Text(
                    text =
                        "/ ${
                            formatter.format(
                                amount
                            )
                        } đ",
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        13.dp
                    )
            )


            /*
            |--------------------------------------------------------------------------
            | PROGRESS
            |--------------------------------------------------------------------------
            */

            LinearProgressIndicator(
                progress = {
                    progress
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            9.dp
                        ),
                color =
                    statusColor,
                trackColor =
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant
            )


            Spacer(
                modifier =
                    Modifier.height(
                        7.dp
                    )
            )


            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        "Đã sử dụng",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )


                Text(
                    text =
                        "${
                            formatBudgetPercent(
                                usagePercentage
                            )
                        }%",
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,
                    fontWeight =
                        FontWeight.ExtraBold,
                    color =
                        statusColor
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            /*
            |--------------------------------------------------------------------------
            | DETAILS
            |--------------------------------------------------------------------------
            */

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(
                        17.dp
                    ),
                color =
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant
                        .copy(
                            alpha = 0.55f
                        )
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            14.dp
                        )
                ) {

                    BudgetDetailRow(
                        title =
                            "Hạn mức",
                        value =
                            "${
                                formatter.format(
                                    amount
                                )
                            } đ"
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    BudgetDetailRow(
                        title =
                            "Đã chi",
                        value =
                            "${
                                formatter.format(
                                    spentAmount
                                )
                            } đ",
                        valueColor =
                            statusColor
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    BudgetDetailRow(
                        title =
                            if (
                                remainingAmount >= 0
                            ) {

                                "Còn lại"

                            } else {

                                "Vượt quá"
                            },
                        value =
                            "${
                                formatter.format(
                                    kotlin.math.abs(
                                        remainingAmount
                                    )
                                )
                            } đ",
                        valueColor =
                            if (
                                remainingAmount >= 0
                            ) {

                                IncomeGreen

                            } else {

                                ExpenseRed
                            }
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            /*
            |--------------------------------------------------------------------------
            | ACTIONS
            |--------------------------------------------------------------------------
            */

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {

                OutlinedButton(
                    onClick =
                        onEditClick,
                    modifier =
                        Modifier
                            .weight(
                                1f
                            )
                            .height(
                                48.dp
                            ),
                    shape =
                        RoundedCornerShape(
                            15.dp
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Edit,
                        contentDescription =
                            null,
                        modifier =
                            Modifier.size(
                                18.dp
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.size(
                                6.dp
                            )
                    )


                    Text(
                        text =
                            "Chỉnh sửa",
                        fontWeight =
                            FontWeight.Bold
                    )
                }


                OutlinedButton(
                    onClick =
                        onDeleteClick,
                    modifier =
                        Modifier
                            .weight(
                                1f
                            )
                            .height(
                                48.dp
                            ),
                    shape =
                        RoundedCornerShape(
                            15.dp
                        ),
                    colors =
                        ButtonDefaults.outlinedButtonColors(
                            contentColor =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        )
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Delete,
                        contentDescription =
                            null,
                        modifier =
                            Modifier.size(
                                18.dp
                            )
                    )


                    Spacer(
                        modifier =
                            Modifier.size(
                                6.dp
                            )
                    )


                    Text(
                        text =
                            "Xóa",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| STATUS BADGE
|--------------------------------------------------------------------------
*/

@Composable
private fun BudgetStatusBadge(
    text: String,
    color: Color
) {

    Surface(
        shape =
            RoundedCornerShape(
                50.dp
            ),
        color =
            color.copy(
                alpha = 0.12f
            )
    ) {

        Text(
            text =
                text,
            modifier =
                Modifier.padding(
                    horizontal = 9.dp,
                    vertical = 5.dp
                ),
            color =
                color,
            style =
                MaterialTheme
                    .typography
                    .labelSmall,
            fontWeight =
                FontWeight.Bold,
            maxLines =
                1
        )
    }
}


/*
|--------------------------------------------------------------------------
| DETAIL ROW
|--------------------------------------------------------------------------
*/

@Composable
private fun BudgetDetailRow(
    title: String,
    value: String,
    valueColor: Color =
        MaterialTheme
            .colorScheme
            .onSurface
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Text(
            text =
                title,
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )


        Text(
            text =
                value,
            style =
                MaterialTheme
                    .typography
                    .bodySmall,
            color =
                valueColor,
            fontWeight =
                FontWeight.Bold
        )
    }
}


/*
|--------------------------------------------------------------------------
| EMPTY STATE
|--------------------------------------------------------------------------
*/

@Composable
private fun BudgetEmptyState(
    onAddClick: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            )
    ) {

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        28.dp
                    ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Surface(
                modifier =
                    Modifier.size(
                        82.dp
                    ),
                shape =
                    RoundedCornerShape(
                        26.dp
                    ),
                color =
                    MaterialTheme
                        .colorScheme
                        .primary
                        .copy(
                            alpha = 0.10f
                        )
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(
                        imageVector =
                            Icons.Default
                                .AccountBalanceWallet,
                        contentDescription =
                            null,
                        modifier =
                            Modifier.size(
                                38.dp
                            ),
                        tint =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            Text(
                text =
                    "Chưa có ngân sách",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.ExtraBold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            Text(
                text =
                    "Tạo ngân sách cho từng danh mục để kiểm soát chi tiêu hiệu quả hơn.",
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                textAlign =
                    TextAlign.Center
            )


            Spacer(
                modifier =
                    Modifier.height(
                        18.dp
                    )
            )


            Button(
                onClick =
                    onAddClick,
                shape =
                    RoundedCornerShape(
                        16.dp
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,
                    contentDescription =
                        null
                )


                Spacer(
                    modifier =
                        Modifier.size(
                            6.dp
                        )
                )


                Text(
                    text =
                        "Tạo ngân sách",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| ERROR STATE
|--------------------------------------------------------------------------
*/

@Composable
private fun BudgetErrorState(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    24.dp
                ),
        contentAlignment =
            Alignment.Center
    ) {

        Card(
            modifier =
                Modifier.fillMaxWidth(),
            shape =
                RoundedCornerShape(
                    22.dp
                ),
            colors =
                CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .errorContainer
                )
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            20.dp
                        ),
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Text(
                    text =
                        "Không thể tải ngân sách",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.ExtraBold,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onErrorContainer
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            6.dp
                        )
                )


                Text(
                    text =
                        message,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onErrorContainer,
                    textAlign =
                        TextAlign.Center
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )


                Button(
                    onClick =
                        onRetry
                ) {

                    Text(
                        text =
                            "Thử lại",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT PERCENT
|--------------------------------------------------------------------------
*/

private fun formatBudgetPercent(
    value: Double
): String {

    return if (
        value % 1.0 == 0.0
    ) {

        value
            .toInt()
            .toString()

    } else {

        String.format(
            Locale.US,
            "%.1f",
            value
        )
    }
}