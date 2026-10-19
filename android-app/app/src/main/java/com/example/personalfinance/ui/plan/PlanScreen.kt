package com.example.personalfinance.ui.plan

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.KeyboardArrowRight

import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.data.model.FinancialPlan
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.viewmodel.PlanViewModel
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.text.NumberFormat
import java.time.YearMonth
import java.util.Locale

private enum class PlanFilter {
    ACTIVE,
    COMPLETED,
    ALL
}


@Composable
fun PlanScreen(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onDetailClick: (Long) -> Unit,
    planViewModel: PlanViewModel = viewModel()
) {

    val state by
    planViewModel.uiState.collectAsState()

    val lifecycleOwner =
        LocalLifecycleOwner.current

    DisposableEffect(
        lifecycleOwner
    ) {

        val observer =
            LifecycleEventObserver { _, event ->

                if (
                    event ==
                    Lifecycle.Event.ON_RESUME
                ) {

                    planViewModel.loadPlans()
                }
            }

        lifecycleOwner.lifecycle.addObserver(
            observer
        )

        onDispose {

            lifecycleOwner.lifecycle.removeObserver(
                observer
            )
        }
    }

    val formatter =
        remember {
            NumberFormat.getNumberInstance(
                Locale("vi", "VN")
            )
        }

    var selectedFilter by remember {
        mutableStateOf(
            PlanFilter.ACTIVE
        )
    }


    /*
    |--------------------------------------------------------------------------
    | FILTER
    |--------------------------------------------------------------------------
    */

    val filteredPlans =
        state.plans
            .filter { plan ->

                when (selectedFilter) {

                    PlanFilter.ACTIVE ->

                        plan.status == "active" ||
                                plan.status == "draft"

                    PlanFilter.COMPLETED ->

                        plan.status == "completed"

                    PlanFilter.ALL ->

                        true
                }
            }
            .sortedWith(
                compareByDescending<FinancialPlan> {
                    it.year
                }.thenByDescending {
                    it.month
                }
            )


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

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 18.dp,
                        end = 15.dp,
                        top = 16.dp,
                        bottom = 14.dp
                    ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text =
                        "Kế hoạch tài chính",
                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,
                    fontWeight =
                        FontWeight.ExtraBold
                )

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )
            }


            Surface(
                modifier =
                    Modifier.size(
                        43.dp
                    ),
                shape =
                    CircleShape,
                color =
                    BrandPurple.copy(
                        alpha = 0.11f
                    )
            ) {

                IconButton(
                    onClick =
                        onAddClick
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Add,
                        contentDescription =
                            "Thêm kế hoạch",
                        tint =
                            BrandPurple,
                        modifier =
                            Modifier.size(
                                25.dp
                            )
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | FILTER
        |--------------------------------------------------------------------------
        */

        PlanFilterBar(
            selected =
                selectedFilter,
            onSelected = {
                selectedFilter = it
            },
            modifier =
                Modifier.padding(
                    horizontal = 16.dp
                )
        )


        Spacer(
            modifier =
                Modifier.height(
                    14.dp
                )
        )


        /*
        |--------------------------------------------------------------------------
        | LOADING
        |--------------------------------------------------------------------------
        */

        if (
            state.isLoading &&
            state.plans.isEmpty()
        ) {

            Box(
                modifier =
                    Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.Center
            ) {

                CircularProgressIndicator(
                    color =
                        BrandPurple
                )
            }

            return@Column
        }


        /*
        |--------------------------------------------------------------------------
        | ERROR
        |--------------------------------------------------------------------------
        */

        if (
            state.error != null &&
            state.plans.isEmpty()
        ) {

            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                horizontalAlignment =
                    Alignment.CenterHorizontally,
                verticalArrangement =
                    Arrangement.Center
            ) {

                Text(
                    text =
                        state.error
                            ?: "Không thể tải kế hoạch.",
                    color =
                        MaterialTheme
                            .colorScheme
                            .error
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            14.dp
                        )
                )

                Button(
                    onClick = {
                        planViewModel.loadPlans()
                    }
                ) {

                    Text("Thử lại")
                }
            }

            return@Column
        }


        /*
        |--------------------------------------------------------------------------
        | EMPTY
        |--------------------------------------------------------------------------
        */

        if (filteredPlans.isEmpty()) {

            PlanEmptyState(
                selectedFilter =
                    selectedFilter,
                onAddClick =
                    onAddClick
            )

            return@Column
        }


        /*
        |--------------------------------------------------------------------------
        | LIST
        |--------------------------------------------------------------------------
        */

        LazyColumn(
            modifier =
                Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 110.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    13.dp
                )
        ) {

            items(
                items =
                    filteredPlans,
                key = {
                    it.id
                }
            ) { plan ->

                FinancialPlanCard(
                    plan =
                        plan,
                    formatter =
                        formatter,
                    onClick = {

                        onDetailClick(
                            plan.id
                        )
                    }
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| FILTER BAR
|--------------------------------------------------------------------------
*/

@Composable
private fun PlanFilterBar(
    selected: PlanFilter,
    onSelected: (PlanFilter) -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier =
            modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                15.dp
            ),
        color =
            MaterialTheme
                .colorScheme
                .surfaceVariant
                .copy(
                    alpha = 0.55f
                )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    3.dp
                )
        ) {

            PlanFilterItem(
                title =
                    "Đang thực hiện",
                selected =
                    selected ==
                            PlanFilter.ACTIVE,
                onClick = {
                    onSelected(
                        PlanFilter.ACTIVE
                    )
                },
                modifier =
                    Modifier.weight(1f)
            )

            PlanFilterItem(
                title =
                    "Hoàn thành",
                selected =
                    selected ==
                            PlanFilter.COMPLETED,
                onClick = {
                    onSelected(
                        PlanFilter.COMPLETED
                    )
                },
                modifier =
                    Modifier.weight(1f)
            )

            PlanFilterItem(
                title =
                    "Tất cả",
                selected =
                    selected ==
                            PlanFilter.ALL,
                onClick = {
                    onSelected(
                        PlanFilter.ALL
                    )
                },
                modifier =
                    Modifier.weight(1f)
            )
        }
    }
}


@Composable
private fun PlanFilterItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier =
            modifier.clickable {
                onClick()
            },
        shape =
            RoundedCornerShape(
                12.dp
            ),
        color =
            if (selected) {
                BrandPurple
            } else {
                Color.Transparent
            }
    ) {

        Box(
            modifier =
                Modifier.padding(
                    vertical = 8.dp
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    title,
                style =
                    MaterialTheme
                        .typography
                        .labelMedium,
                color =
                    if (selected) {
                        Color.White
                    } else {
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                    },
                fontWeight =
                    if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    },
                maxLines = 1
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| PLAN CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun FinancialPlanCard(
    plan: FinancialPlan,
    formatter: NumberFormat,
    onClick: () -> Unit
) {

    /*
    |--------------------------------------------------------------------------
    | VALUES
    |--------------------------------------------------------------------------
    */

    val plannedExpense =
        plan.fixed_expense +
                plan.flexible_budget

    val remaining =
        (
                plan.monthly_income -
                        plannedExpense
                )
            .coerceAtLeast(
                0.0
            )

    val expensePercentage =
        if (plan.monthly_income > 0) {

            (
                    plannedExpense /
                            plan.monthly_income *
                            100.0
                    )
                .coerceIn(
                    0.0,
                    100.0
                )

        } else {

            0.0
        }

    val progress =
        (
                expensePercentage /
                        100.0
                )
            .toFloat()


    val statusText =
        getPlanStatusText(
            plan
        )

    val statusStyle =
        getPlanStatusStyle(
            statusText
        )


    val yearMonth =
        try {

            YearMonth.of(
                plan.year,
                plan.month
            )

        } catch (_: Exception) {

            null
        }


    val dateRange =
        if (yearMonth != null) {

            val month =
                plan.month
                    .toString()
                    .padStart(
                        2,
                        '0'
                    )

            val lastDay =
                yearMonth
                    .lengthOfMonth()
                    .toString()
                    .padStart(
                        2,
                        '0'
                    )

            "01/$month/${plan.year} - $lastDay/$month/${plan.year}"

        } else {

            "Tháng ${plan.month}/${plan.year}"
        }


    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable {
                    onClick()
                },
        shape =
            RoundedCornerShape(
                21.dp
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
                        15.dp
                    )
        ) {


            /*
            |--------------------------------------------------------------------------
            | TITLE
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
                            46.dp
                        ),
                    shape =
                        RoundedCornerShape(
                            15.dp
                        ),
                    color =
                        statusStyle
                            .softColor
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                statusStyle.icon,
                            contentDescription =
                                null,
                            tint =
                                statusStyle.mainColor,
                            modifier =
                                Modifier.size(
                                    24.dp
                                )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.width(
                            11.dp
                        )
                )


                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            plan.plan_name
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Kế hoạch tháng ${plan.month}, ${plan.year}",
                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,
                        fontWeight =
                            FontWeight.ExtraBold,
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

                    Text(
                        text =
                            dateRange,
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )


                Surface(
                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),
                    color =
                        statusStyle.softColor
                ) {

                    Text(
                        text =
                            statusText,
                        modifier =
                            Modifier.padding(
                                horizontal = 9.dp,
                                vertical = 5.dp
                            ),
                        color =
                            statusStyle.mainColor,
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
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


            /*
            |--------------------------------------------------------------------------
            | MONEY
            |--------------------------------------------------------------------------
            */

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
                        .copy(
                            alpha = 0.38f
                        )
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            13.dp
                        )
                ) {

                    PlanMoneyRow(
                        title =
                            "Thu nhập dự kiến",
                        value =
                            "${formatter.format(plan.monthly_income)} đ",
                        valueColor =
                            IncomeGreen
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                9.dp
                            )
                    )


                    PlanMoneyRow(
                        title =
                            "Chi tiêu dự kiến",
                        value =
                            "${formatter.format(plannedExpense)} đ",
                        valueColor =
                            ExpenseRed
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                10.dp
                            )
                    )


                    HorizontalDivider(
                        color =
                            MaterialTheme
                                .colorScheme
                                .outlineVariant
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                10.dp
                            )
                    )


                    PlanMoneyRow(
                        title =
                            "Còn lại",
                        value =
                            "${formatter.format(remaining)} đ",
                        valueColor =
                            BrandPurple,
                        bold =
                            true
                    )
                }
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

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                LinearProgressIndicator(
                    progress = {
                        progress
                    },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(7.dp)
                            .clip(
                                RoundedCornerShape(
                                    20.dp
                                )
                            ),
                    color =
                        BrandPurple,
                    trackColor =
                        BrandPurple.copy(
                            alpha = 0.10f
                        )
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            9.dp
                        )
                )


                Text(
                    text =
                        "${formatPercentage(expensePercentage)}%",
                    color =
                        BrandPurple,
                    style =
                        MaterialTheme
                            .typography
                            .labelMedium,
                    fontWeight =
                        FontWeight.ExtraBold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            /*
            |--------------------------------------------------------------------------
            | SAVING TARGET
            |--------------------------------------------------------------------------
            */

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Text(
                    text =
                        "Mục tiêu tiết kiệm",
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
                        "${formatter.format(plan.saving_target)} đ",
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        IncomeGreen,
                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        11.dp
                    )
            )


            HorizontalDivider(
                color =
                    MaterialTheme
                        .colorScheme
                        .outlineVariant
                        .copy(
                            alpha = 0.55f
                        )
            )


            /*
            |--------------------------------------------------------------------------
            | DETAIL
            |--------------------------------------------------------------------------
            */

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 11.dp
                        ),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Icon(
                    imageVector =
                        Icons.Default.EventNote,
                    contentDescription =
                        null,
                    tint =
                        BrandPurple,
                    modifier =
                        Modifier.size(
                            19.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )


                Text(
                    text =
                        if (plan.allocation_count > 0) {

                            "${plan.allocation_count} danh mục phân bổ"

                        } else {

                            "Chưa có phân bổ"
                        },

                    modifier =
                        Modifier.weight(1f),

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    fontWeight =
                        FontWeight.SemiBold
                )


                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Xem chi tiết",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| MONEY ROW
|--------------------------------------------------------------------------
*/

@Composable
private fun PlanMoneyRow(
    title: String,
    value: String,
    valueColor: Color,
    bold: Boolean = false
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


        Spacer(
            modifier =
                Modifier.width(
                    12.dp
                )
        )


        Text(
            text =
                value,
            color =
                valueColor,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            fontWeight =
                if (bold) {
                    FontWeight.ExtraBold
                } else {
                    FontWeight.Bold
                }
        )
    }
}


/*
|--------------------------------------------------------------------------
| EMPTY
|--------------------------------------------------------------------------
*/

@Composable
private fun PlanEmptyState(
    selectedFilter: PlanFilter,
    onAddClick: () -> Unit
) {

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    30.dp
                ),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {

        Surface(
            modifier =
                Modifier.size(
                    72.dp
                ),
            shape =
                CircleShape,
            color =
                BrandPurple.copy(
                    alpha = 0.10f
                )
        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.EventNote,
                    contentDescription =
                        null,
                    tint =
                        BrandPurple,
                    modifier =
                        Modifier.size(
                            35.dp
                        )
                )
            }
        }


        Spacer(
            modifier =
                Modifier.height(
                    15.dp
                )
        )


        Text(
            text =
                when (selectedFilter) {

                    PlanFilter.ACTIVE ->
                        "Chưa có kế hoạch đang thực hiện"

                    PlanFilter.COMPLETED ->
                        "Chưa có kế hoạch hoàn thành"

                    PlanFilter.ALL ->
                        "Chưa có kế hoạch tài chính"
                },
            style =
                MaterialTheme
                    .typography
                    .titleMedium,
            fontWeight =
                FontWeight.Bold
        )


        Spacer(
            modifier =
                Modifier.height(
                    6.dp
                )
        )
        if (
            selectedFilter !=
            PlanFilter.COMPLETED
        ) {

            Spacer(
                modifier =
                    Modifier.height(
                        17.dp
                    )
            )


            Button(
                onClick =
                    onAddClick,
                shape =
                    RoundedCornerShape(
                        15.dp
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
                        Modifier.width(
                            6.dp
                        )
                )

                Text(
                    "Tạo kế hoạch"
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

private fun getPlanStatusText(
    plan: FinancialPlan
): String {

    return when (
        plan.status
            .trim()
            .lowercase()
    ) {

        "active" ->
            "Đang thực hiện"

        "completed" ->
            "Hoàn thành"

        "cancelled" ->
            "Đã hủy"

        "draft" ->
            "Sắp tới"

        else ->
            plan.status
    }
}


private data class PlanStatusStyle(
    val mainColor: Color,
    val softColor: Color,
    val icon: ImageVector
)


private fun getPlanStatusStyle(
    status: String
): PlanStatusStyle {

    return when (status) {

        "Hoàn thành" ->

            PlanStatusStyle(
                mainColor =
                    IncomeGreen,
                softColor =
                    IncomeGreen.copy(
                        alpha = 0.11f
                    ),
                icon =
                    Icons.Default.CheckCircle
            )


        "Sắp tới" ->

            PlanStatusStyle(
                mainColor =
                    Color(
                        0xFF3488F6
                    ),
                softColor =
                    Color(
                        0xFF3488F6
                    ).copy(
                        alpha = 0.11f
                    ),
                icon =
                    Icons.Default.CalendarMonth
            )


        "Đã hủy" ->

            PlanStatusStyle(
                mainColor =
                    ExpenseRed,
                softColor =
                    ExpenseRed.copy(
                        alpha = 0.10f
                    ),
                icon =
                    Icons.Default.EventNote
            )


        else ->

            PlanStatusStyle(
                mainColor =
                    BrandPurple,
                softColor =
                    BrandPurple.copy(
                        alpha = 0.10f
                    ),
                icon =
                    Icons.Default.EventNote
            )
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT
|--------------------------------------------------------------------------
*/

private fun formatPercentage(
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
            Locale("vi", "VN"),
            "%.1f",
            value
        )
    }
}