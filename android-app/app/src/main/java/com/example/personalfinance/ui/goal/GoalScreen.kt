package com.example.personalfinance.ui.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Schedule

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.ui.theme.BrandGold
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.ui.theme.WarningOrange
import com.example.personalfinance.viewmodel.GoalViewModel

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun GoalScreen(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onDetailClick: (Long) -> Unit,
    goalViewModel: GoalViewModel = viewModel()
) {

    val state by
    goalViewModel.uiState.collectAsState()


    val formatter =
        remember {
            NumberFormat.getNumberInstance(
                Locale(
                    "vi",
                    "VN"
                )
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

        GoalHeader(
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

                GoalErrorState(
                    message =
                        state.error
                            ?: "Đã xảy ra lỗi.",
                    onRetry = {

                        goalViewModel
                            .loadGoals()
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | EMPTY
            |--------------------------------------------------------------------------
            */

            state.goals.isEmpty() -> {

                GoalEmptyState(
                    onAddClick =
                        onAddClick
                )
            }


            /*
            |--------------------------------------------------------------------------
            | DATA
            |--------------------------------------------------------------------------
            */

            else -> {

                val completedGoals =
                    state.goals.count {
                        it.status ==
                                "completed"
                    }

                val activeGoals =
                    state.goals.count {
                        it.status ==
                                "active"
                    }


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

                        GoalSummaryCard(
                            totalGoals =
                                state.goals.size,
                            activeGoals =
                                activeGoals,
                            completedGoals =
                                completedGoals
                        )
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | TITLE
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
                                    "Danh sách mục tiêu",
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
                                    "${state.goals.size} mục tiêu",
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
                    | LIST
                    |--------------------------------------------------------------------------
                    */

                    items(
                        items =
                            state.goals,
                        key = { goal ->
                            goal.id
                        }
                    ) { goal ->


                        val progress =
                            (
                                    goal
                                        .progress_percentage
                                        .toFloat() / 100f
                                    ).coerceIn(
                                    0f,
                                    1f
                                )


                        val progressColor =
                            when {

                                goal.status ==
                                        "completed" -> {

                                    IncomeGreen
                                }


                                goal.status ==
                                        "cancelled" -> {

                                    ExpenseRed
                                }


                                goal.progress_percentage >=
                                        80 -> {

                                    IncomeGreen
                                }


                                goal.progress_percentage >=
                                        50 -> {

                                    WarningOrange
                                }


                                else -> {

                                    MaterialTheme
                                        .colorScheme
                                        .primary
                                }
                            }


                        val statusText =
                            when (
                                goal.status
                            ) {

                                "active" ->
                                    "Đang thực hiện"

                                "completed" ->
                                    "Hoàn thành"

                                "cancelled" ->
                                    "Đã hủy"

                                else ->
                                    goal.status
                            }


                        GoalItemCard(
                            name =
                                goal.name,
                            description =
                                goal.description,
                            deadline =
                                goal.deadline,
                            currentAmount =
                                goal.current_amount,
                            targetAmount =
                                goal.target_amount,
                            remainingAmount =
                                goal.remaining_amount,
                            monthsRemaining =
                                goal.months_remaining,
                            requiredPerMonth =
                                goal.required_per_month,
                            progressPercentage =
                                goal.progress_percentage,
                            progress =
                                progress,
                            status =
                                goal.status,
                            statusText =
                                statusText,
                            progressColor =
                                progressColor,
                            formatter =
                                formatter,
                            onClick = {

                                onDetailClick(
                                    goal.id
                                )
                            }
                        )
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
private fun GoalHeader(
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
                        "Quay lại",
                    tint =
                        MaterialTheme
                            .colorScheme
                            .onSurface
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
                    .weight(1f)
                    .padding(
                        start = 13.dp,
                        end = 8.dp
                    )
        ) {

            Text(
                text =
                    "Mục tiêu tài chính",
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
                    "Theo dõi hành trình tích lũy",
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
                ),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .primary
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
| SUMMARY CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalSummaryCard(
    totalGoals: Int,
    activeGoals: Int,
    completedGoals: Int
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                24.dp
            ),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .primaryContainer
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

            Text(
                text =
                    "Tổng quan mục tiêu",
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold,
                color =
                    MaterialTheme
                        .colorScheme
                        .onPrimaryContainer
            )


            Spacer(
                modifier =
                    Modifier.height(
                        15.dp
                    )
            )


            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        8.dp
                    )
            ) {

                GoalSummaryItem(
                    value =
                        totalGoals.toString(),
                    title =
                        "Tổng mục tiêu",
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary,
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                GoalSummaryItem(
                    value =
                        activeGoals.toString(),
                    title =
                        "Đang làm",
                    color =
                        BrandGold,
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                GoalSummaryItem(
                    value =
                        completedGoals.toString(),
                    title =
                        "Hoàn thành",
                    color =
                        IncomeGreen,
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| SUMMARY ITEM
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalSummaryItem(
    value: String,
    title: String,
    color: Color,
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
            MaterialTheme
                .colorScheme
                .surface
                .copy(
                    alpha = 0.75f
                )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    horizontal = 6.dp,
                    vertical = 13.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    value,
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
                fontWeight =
                    FontWeight.ExtraBold,
                color =
                    color
            )


            Spacer(
                modifier =
                    Modifier.height(
                        3.dp
                    )
            )


            Text(
                text =
                    title,
                style =
                    MaterialTheme
                        .typography
                        .labelSmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                textAlign =
                    TextAlign.Center,
                maxLines =
                    1
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| GOAL ITEM
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalItemCard(
    name: String,
    description: String?,
    deadline: String,
    currentAmount: Double,
    targetAmount: Double,
    remainingAmount: Double,
    monthsRemaining: Int,
    requiredPerMonth: Double,
    progressPercentage: Double,
    progress: Float,
    status: String,
    statusText: String,
    progressColor: Color,
    formatter: NumberFormat,
    onClick: () -> Unit
) {

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
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


                /*
                |--------------------------------------------------------------------------
                | ICON
                |--------------------------------------------------------------------------
                */

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
                        progressColor.copy(
                            alpha = 0.11f
                        )
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                if (
                                    status ==
                                    "completed"
                                ) {

                                    Icons.Default
                                        .CheckCircle

                                } else {

                                    Icons.Default
                                        .Flag
                                },
                            contentDescription =
                                null,
                            tint =
                                progressColor,
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


                /*
                |--------------------------------------------------------------------------
                | NAME
                |--------------------------------------------------------------------------
                */

                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            name,
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
                                Icons.Default.Schedule,
                            contentDescription =
                                null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,
                            modifier =
                                Modifier.size(
                                    14.dp
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.size(
                                    4.dp
                                )
                        )


                        Text(
                            text =
                                "Hạn ${formatGoalListDate(deadline)}",
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


                GoalStatusBadge(
                    text =
                        statusText,
                    color =
                        progressColor
                )
            }


            /*
            |--------------------------------------------------------------------------
            | DESCRIPTION
            |--------------------------------------------------------------------------
            */

            if (
                !description.isNullOrBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(
                            12.dp
                        )
                )


                Text(
                    text =
                        description,
                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                    maxLines =
                        2,
                    overflow =
                        TextOverflow.Ellipsis
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
                    "Đã tích lũy",
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
                                currentAmount
                            )
                        } đ",
                    style =
                        MaterialTheme
                            .typography
                            .titleLarge,
                    fontWeight =
                        FontWeight.ExtraBold,
                    color =
                        progressColor
                )


                Text(
                    text =
                        "/ ${
                            formatter.format(
                                targetAmount
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
                    progressColor,
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
                        "Tiến độ",
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
                        "${formatGoalPercent(progressPercentage)}%",
                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,
                    fontWeight =
                        FontWeight.ExtraBold,
                    color =
                        progressColor
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

                    GoalDetailRow(
                        title =
                            "Còn thiếu",
                        value =
                            "${
                                formatter.format(
                                    remainingAmount
                                        .coerceAtLeast(
                                            0.0
                                        )
                                )
                            } đ"
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    GoalDetailRow(
                        title =
                            "Thời gian còn lại",
                        value =
                            if (
                                status ==
                                "completed"
                            ) {

                                "Đã hoàn thành"

                            } else if (
                                monthsRemaining <= 0
                            ) {

                                "Dưới 1 tháng"

                            } else {

                                "$monthsRemaining tháng"
                            }
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    GoalDetailRow(
                        title =
                            "Cần tiết kiệm/tháng",
                        value =
                            if (
                                status ==
                                "completed"
                            ) {

                                "0 đ"

                            } else {

                                "${
                                    formatter.format(
                                        requiredPerMonth
                                    )
                                } đ"
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
            | DETAIL BUTTON
            |--------------------------------------------------------------------------
            */

            Button(
                onClick =
                    onClick,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            48.dp
                        ),
                shape =
                    RoundedCornerShape(
                        15.dp
                    ),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )
            ) {

                Text(
                    text =
                        "Xem chi tiết",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| STATUS
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalStatusBadge(
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
                alpha = 0.11f
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
private fun GoalDetailRow(
    title: String,
    value: String
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
            fontWeight =
                FontWeight.Bold,
            color =
                MaterialTheme
                    .colorScheme
                    .onSurface
        )
    }
}


/*
|--------------------------------------------------------------------------
| EMPTY
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalEmptyState(
    onAddClick: () -> Unit
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

        Column(
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
                            Icons.Default.Flag,
                        contentDescription =
                            null,
                        tint =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        modifier =
                            Modifier.size(
                                38.dp
                            )
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
                    "Chưa có mục tiêu nào",
                style =
                    MaterialTheme
                        .typography
                        .titleLarge,
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
                        7.dp
                    )
            )


            Text(
                text =
                    "Tạo mục tiêu đầu tiên để bắt đầu\nhành trình tích lũy của bạn.",
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
                        "Tạo mục tiêu",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| ERROR
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalErrorState(
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
                        "Không thể tải mục tiêu",
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
| FORMAT DATE
|--------------------------------------------------------------------------
*/

private fun formatGoalListDate(
    date: String
): String {

    return try {

        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

        input.isLenient =
            false


        val output =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale(
                    "vi",
                    "VN"
                )
            )


        val parsed =
            input.parse(
                date
            )


        if (parsed != null) {

            output.format(
                parsed
            )

        } else {

            date
        }

    } catch (_: Exception) {

        date
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT PERCENT
|--------------------------------------------------------------------------
*/

private fun formatGoalPercent(
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