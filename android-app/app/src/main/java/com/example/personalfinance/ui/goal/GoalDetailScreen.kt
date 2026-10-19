package com.example.personalfinance.ui.goal

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.ui.theme.WarningOrange
import com.example.personalfinance.viewmodel.GoalViewModel

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun GoalDetailScreen(
    goalId: Long,
    onBack: () -> Unit,
    onAddContributionClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    goalViewModel: GoalViewModel = viewModel()
) {

    val goal by
    goalViewModel.selectedGoal.collectAsState()

    val contributions by
    goalViewModel.contributions.collectAsState()

    val message by
    goalViewModel.message.collectAsState()


    val formatter =
        remember {
            NumberFormat.getNumberInstance(
                Locale(
                    "vi",
                    "VN"
                )
            )
        }


    var showDeleteDialog by remember {
        mutableStateOf(false)
    }


    /*
    |--------------------------------------------------------------------------
    | LOAD
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(goalId) {

        goalViewModel.loadGoalDetail(
            goalId
        )

        goalViewModel.loadContributions(
            goalId
        )
    }


    /*
    |--------------------------------------------------------------------------
    | DELETE DIALOG
    |--------------------------------------------------------------------------
    */

    if (showDeleteDialog) {

        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {

                Text(
                    text = "Xóa mục tiêu?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {

                Text(
                    text =
                        "Mục tiêu và các dữ liệu liên quan có thể bị xóa. Bạn có chắc muốn tiếp tục?"
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        showDeleteDialog = false

                        goalViewModel.deleteGoal(
                            goalId
                        ) {
                            onBack()
                        }
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .error
                        )
                ) {

                    Text(
                        "Xóa mục tiêu"
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {
                        showDeleteDialog = false
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

        GoalDetailHeader(
            onBack = onBack
        )


        /*
        |--------------------------------------------------------------------------
        | LOADING
        |--------------------------------------------------------------------------
        */

        if (goal == null) {

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

            return@Column
        }


        val data =
            goal!!


        /*
        |--------------------------------------------------------------------------
        | STATUS
        |--------------------------------------------------------------------------
        */

        val progress =
            (
                    data.progress_percentage
                        .toFloat() / 100f
                    ).coerceIn(
                    0f,
                    1f
                )


        val progressColor =
            when {

                data.status ==
                        "completed" -> {

                    IncomeGreen
                }

                data.status ==
                        "cancelled" -> {

                    ExpenseRed
                }

                data.progress_percentage >=
                        80 -> {

                    IncomeGreen
                }

                data.progress_percentage >=
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
                data.status
            ) {

                "active" ->
                    "Đang thực hiện"

                "completed" ->
                    "Hoàn thành"

                "cancelled" ->
                    "Đã hủy"

                else ->
                    data.status
            }


        /*
        |--------------------------------------------------------------------------
        | CONTENT
        |--------------------------------------------------------------------------
        */

        LazyColumn(
            modifier =
                Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = 18.dp,
                    end = 18.dp,
                    top = 6.dp,
                    bottom = 40.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    14.dp
                )
        ) {


            /*
            |--------------------------------------------------------------------------
            | MAIN CARD
            |--------------------------------------------------------------------------
            */

            item {

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
                                    18.dp
                                )
                    ) {


                        /*
                        |--------------------------------------------------------------------------
                        | GOAL HEADER
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
                                        54.dp
                                    ),
                                shape =
                                    RoundedCornerShape(
                                        17.dp
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
                                                data.status ==
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
                                                27.dp
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
                                        data.name,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleLarge,
                                    fontWeight =
                                        FontWeight.ExtraBold,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurface,
                                    maxLines =
                                        2,
                                    overflow =
                                        TextOverflow.Ellipsis
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(
                                            4.dp
                                        )
                                )


                                GoalDetailStatusBadge(
                                    text =
                                        statusText,
                                    color =
                                        progressColor
                                )
                            }
                        }


                        /*
                        |--------------------------------------------------------------------------
                        | DESCRIPTION
                        |--------------------------------------------------------------------------
                        */

                        if (
                            !data.description
                                .isNullOrBlank()
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        15.dp
                                    )
                            )


                            Text(
                                text =
                                    data.description
                                        ?: "",
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
                                    18.dp
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


                        Text(
                            text =
                                "${
                                    formatter.format(
                                        data.current_amount
                                    )
                                } đ",
                            style =
                                MaterialTheme
                                    .typography
                                    .headlineSmall,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color =
                                progressColor
                        )


                        Text(
                            text =
                                "Mục tiêu ${
                                    formatter.format(
                                        data.target_amount
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


                        Spacer(
                            modifier =
                                Modifier.height(
                                    14.dp
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
                                        10.dp
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
                                    8.dp
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
                                    "${
                                        formatGoalDetailPercent(
                                            data.progress_percentage
                                        )
                                    }%",
                                fontWeight =
                                    FontWeight.ExtraBold,
                                color =
                                    progressColor
                            )
                        }
                    }
                }
            }


            /*
            |--------------------------------------------------------------------------
            | FINANCIAL DETAILS
            |--------------------------------------------------------------------------
            */

            item {

                GoalDetailSectionCard(
                    title =
                        "Kế hoạch tích lũy"
                ) {

                    GoalDetailInfoRow(
                        title =
                            "Số tiền mục tiêu",
                        value =
                            "${
                                formatter.format(
                                    data.target_amount
                                )
                            } đ"
                    )


                    GoalDetailDivider()


                    GoalDetailInfoRow(
                        title =
                            "Đã tích lũy",
                        value =
                            "${
                                formatter.format(
                                    data.current_amount
                                )
                            } đ",
                        valueColor =
                            progressColor
                    )


                    GoalDetailDivider()


                    GoalDetailInfoRow(
                        title =
                            "Còn thiếu",
                        value =
                            "${
                                formatter.format(
                                    data.remaining_amount
                                        .coerceAtLeast(
                                            0.0
                                        )
                                )
                            } đ"
                    )


                    GoalDetailDivider()


                    GoalDetailInfoRow(
                        title =
                            "Cần tiết kiệm/tháng",
                        value =
                            if (
                                data.status ==
                                "completed"
                            ) {

                                "0 đ"

                            } else {

                                "${
                                    formatter.format(
                                        data.required_per_month
                                    )
                                } đ"
                            }
                    )


                    GoalDetailDivider()


                    GoalDetailInfoRow(
                        title =
                            "Thời gian còn lại",
                        value =
                            if (
                                data.status ==
                                "completed"
                            ) {

                                "Đã hoàn thành"

                            } else if (
                                data.months_remaining <= 0
                            ) {

                                "Dưới 1 tháng"

                            } else {

                                "${data.months_remaining} tháng"
                            }
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | TIME
            |--------------------------------------------------------------------------
            */

            item {

                GoalDetailSectionCard(
                    title =
                        "Thời gian thực hiện"
                ) {

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
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary,
                            modifier =
                                Modifier.size(
                                    20.dp
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.size(
                                    10.dp
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
                                    "Ngày bắt đầu",
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
                                    formatGoalDetailDate(
                                        data.start_date
                                    ),
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(
                                14.dp
                            )
                    )


                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default
                                    .Flag,
                            contentDescription =
                                null,
                            tint =
                                progressColor,
                            modifier =
                                Modifier.size(
                                    20.dp
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.size(
                                    10.dp
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
                                    "Hạn hoàn thành",
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
                                    formatGoalDetailDate(
                                        data.deadline
                                    ),
                                fontWeight =
                                    FontWeight.Bold
                            )
                        }
                    }
                }
            }


            /*
            |--------------------------------------------------------------------------
            | ADD CONTRIBUTION
            |--------------------------------------------------------------------------
            */

            if (
                data.status ==
                "active"
            ) {

                item {

                    Button(
                        onClick = {

                            onAddContributionClick(
                                goalId
                            )
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    56.dp
                                ),
                        shape =
                            RoundedCornerShape(
                                18.dp
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
                                    7.dp
                                )
                        )


                        Text(
                            text =
                                "Thêm tiền tiết kiệm",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }


            /*
            |--------------------------------------------------------------------------
            | CONTRIBUTION TITLE
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
                            "Lịch sử đóng góp",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.ExtraBold
                    )


                    Text(
                        text =
                            "${contributions.size} khoản",
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
            | EMPTY CONTRIBUTION
            |--------------------------------------------------------------------------
            */

            if (
                contributions.isEmpty()
            ) {

                item {

                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                20.dp
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
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        22.dp
                                    ),
                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Surface(
                                modifier =
                                    Modifier.size(
                                        54.dp
                                    ),
                                shape =
                                    CircleShape,
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
                                            Icons.Default.Add,
                                        contentDescription =
                                            null,
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
                                        10.dp
                                    )
                            )


                            Text(
                                text =
                                    "Chưa có khoản đóng góp",
                                fontWeight =
                                    FontWeight.Bold
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        4.dp
                                    )
                            )


                            Text(
                                text =
                                    "Các khoản tiền bạn tiết kiệm cho mục tiêu sẽ xuất hiện tại đây.",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,
                                textAlign =
                                    TextAlign.Center
                            )
                        }
                    }
                }

            } else {


                /*
                |--------------------------------------------------------------------------
                | CONTRIBUTION LIST
                |--------------------------------------------------------------------------
                */

                items(
                    items =
                        contributions
                ) { contribution ->

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                20.dp
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
                                    1.dp
                            )
                    ) {

                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        15.dp
                                    ),
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Surface(
                                modifier =
                                    Modifier.size(
                                        44.dp
                                    ),
                                shape =
                                    RoundedCornerShape(
                                        14.dp
                                    ),
                                color =
                                    IncomeGreen.copy(
                                        alpha = 0.10f
                                    )
                            ) {

                                Box(
                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    Text(
                                        text =
                                            "+",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleLarge,
                                        fontWeight =
                                            FontWeight.ExtraBold,
                                        color =
                                            IncomeGreen
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
                                        formatGoalDetailDate(
                                            contribution
                                                .contribution_date
                                        ),
                                    fontWeight =
                                        FontWeight.Bold
                                )


                                if (
                                    !contribution.note
                                        .isNullOrBlank()
                                ) {

                                    Spacer(
                                        modifier =
                                            Modifier.height(
                                                3.dp
                                            )
                                    )


                                    Text(
                                        text =
                                            contribution.note
                                                ?: "",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodySmall,
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
                            }


                            Text(
                                text =
                                    "+${
                                        formatter.format(
                                            contribution.amount
                                        )
                                    } đ",
                                fontWeight =
                                    FontWeight.ExtraBold,
                                color =
                                    IncomeGreen
                            )
                        }
                    }
                }
            }


            /*
            |--------------------------------------------------------------------------
            | MESSAGE
            |--------------------------------------------------------------------------
            */

            if (
                !message.isNullOrBlank()
            ) {

                item {

                    Surface(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                16.dp
                            ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .surfaceVariant
                    ) {

                        Text(
                            text =
                                message ?: "",
                            modifier =
                                Modifier.padding(
                                    14.dp
                                ),
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
            }


            /*
            |--------------------------------------------------------------------------
            | ACTIONS
            |--------------------------------------------------------------------------
            */

            item {

                Text(
                    text =
                        "Quản lý mục tiêu",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.ExtraBold
                )
            }


            item {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )
                ) {


                    /*
                    |--------------------------------------------------------------------------
                    | EDIT
                    |--------------------------------------------------------------------------
                    */

                    OutlinedButton(
                        onClick = {

                            onEditClick(
                                goalId
                            )
                        },
                        modifier =
                            Modifier
                                .weight(
                                    1f
                                )
                                .height(
                                    52.dp
                                ),
                        shape =
                            RoundedCornerShape(
                                16.dp
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


                    /*
                    |--------------------------------------------------------------------------
                    | DELETE
                    |--------------------------------------------------------------------------
                    */

                    OutlinedButton(
                        onClick = {

                            showDeleteDialog =
                                true
                        },
                        modifier =
                            Modifier
                                .weight(
                                    1f
                                )
                                .height(
                                    52.dp
                                ),
                        shape =
                            RoundedCornerShape(
                                16.dp
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
}


/*
|--------------------------------------------------------------------------
| HEADER
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalDetailHeader(
    onBack: () -> Unit
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


        Column(
            modifier =
                Modifier.padding(
                    start = 14.dp
                )
        ) {

            Text(
                text =
                    "Chi tiết mục tiêu",
                style =
                    MaterialTheme
                        .typography
                        .headlineSmall,
                fontWeight =
                    FontWeight.ExtraBold
            )


            Text(
                text =
                    "Theo dõi tiến độ và quá trình tích lũy",
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
}


/*
|--------------------------------------------------------------------------
| SECTION CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalDetailSectionCard(
    title: String,
    content:
    @Composable () -> Unit
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

            Text(
                text =
                    title,
                style =
                    MaterialTheme
                        .typography
                        .titleMedium,
                fontWeight =
                    FontWeight.ExtraBold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        14.dp
                    )
            )


            content()
        }
    }
}


/*
|--------------------------------------------------------------------------
| INFO ROW
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalDetailInfoRow(
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
                    .bodyMedium,
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
                    .bodyMedium,
            fontWeight =
                FontWeight.Bold,
            color =
                valueColor
        )
    }
}


/*
|--------------------------------------------------------------------------
| DIVIDER
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalDetailDivider() {

    HorizontalDivider(
        modifier =
            Modifier.padding(
                vertical = 10.dp
            ),
        color =
            MaterialTheme
                .colorScheme
                .outlineVariant
                .copy(
                    alpha = 0.6f
                )
    )
}


/*
|--------------------------------------------------------------------------
| STATUS BADGE
|--------------------------------------------------------------------------
*/

@Composable
private fun GoalDetailStatusBadge(
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
                    horizontal = 10.dp,
                    vertical = 5.dp
                ),
            style =
                MaterialTheme
                    .typography
                    .labelSmall,
            fontWeight =
                FontWeight.Bold,
            color =
                color
        )
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT DATE
|--------------------------------------------------------------------------
*/

private fun formatGoalDetailDate(
    date: String
): String {

    return try {

        val inputFormat =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.US
            )

        inputFormat.isLenient =
            false


        val outputFormat =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale(
                    "vi",
                    "VN"
                )
            )


        val parsedDate =
            inputFormat.parse(
                date
            )


        if (
            parsedDate != null
        ) {

            outputFormat.format(
                parsedDate
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

private fun formatGoalDetailPercent(
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