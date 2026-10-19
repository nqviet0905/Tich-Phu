package com.example.personalfinance.ui.plan

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.PieChart

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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.viewmodel.PlanViewModel

import java.text.NumberFormat
import java.time.YearMonth
import java.util.Locale


@Composable
fun PlanDetailScreen(
    planId: Long,
    onBack: () -> Unit,
    onAdjustClick: (Long) -> Unit,
    planViewModel: PlanViewModel = viewModel()
) {

    val plan by
    planViewModel.selectedPlan.collectAsState()

    val lifecycleOwner =
        LocalLifecycleOwner.current

    DisposableEffect(
        lifecycleOwner,
        planId
    ) {

        val observer =
            LifecycleEventObserver { _, event ->

                if (
                    event ==
                    Lifecycle.Event.ON_RESUME
                ) {

                    planViewModel.loadPlanDetail(
                        planId
                    )
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

    val message by
    planViewModel.message.collectAsState()

    val formatter =
        remember {
            NumberFormat.getNumberInstance(
                Locale("vi", "VN")
            )
        }

    /*
    |--------------------------------------------------------------------------
    | LOADING
    |--------------------------------------------------------------------------
    */

    if (plan == null) {

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        MaterialTheme
                            .colorScheme
                            .background
                    ),
            contentAlignment =
                Alignment.Center
        ) {

            CircularProgressIndicator(
                color = BrandPurple
            )
        }

        return
    }


    val data = plan!!


    /*
    |--------------------------------------------------------------------------
    | CALCULATE
    |--------------------------------------------------------------------------
    */

    val totalExpense =
        data.fixed_expense +
                data.flexible_budget


    val remaining =
        (
                data.monthly_income -
                        totalExpense
                )
            .coerceAtLeast(
                0.0
            )


    val expensePercent =
        if (data.monthly_income > 0) {

            (
                    totalExpense /
                            data.monthly_income *
                            100.0
                    )
                .coerceIn(
                    0.0,
                    100.0
                )

        } else {

            0.0
        }


    val mainProgress =
        (
                expensePercent /
                        100.0
                )
            .toFloat()


    val planStatus =
        getDetailStatus(
            month = data.month,
            year = data.year
        )


    val yearMonth =
        try {

            YearMonth.of(
                data.year,
                data.month
            )

        } catch (_: Exception) {

            null
        }


    val dateRange =
        if (yearMonth != null) {

            val month =
                data.month
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

            "01/$month/${data.year} - $lastDay/$month/${data.year}"

        } else {

            "Tháng ${data.month}/${data.year}"
        }


    /*
    |--------------------------------------------------------------------------
    | MAIN
    |--------------------------------------------------------------------------
    */

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                ),
        contentPadding =
            PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 10.dp,
                bottom = 34.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(
                14.dp
            )
    ) {


        /*
        |--------------------------------------------------------------------------
        | HEADER
        |--------------------------------------------------------------------------
        */

        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(
                            42.dp
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
                        onClick = onBack
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,
                            contentDescription =
                                "Quay lại",
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .onSurface
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.width(
                            12.dp
                        )
                )


                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            data.plan_name
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: "Kế hoạch tháng ${data.month}, ${data.year}",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
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
                                2.dp
                            )
                    )

                    Text(
                        text = dateRange,
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
        | OVERVIEW
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
                                                    0xFF825DFF
                                                )
                                            )
                                    ),
                                shape =
                                    RoundedCornerShape(
                                        24.dp
                                    )
                            )
                            .padding(
                                18.dp
                            )
                ) {

                    Column {


                        /*
                        |--------------------------------------------------------------------------
                        | PLAN NAME + STATUS
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
                                        45.dp
                                    ),
                                shape =
                                    RoundedCornerShape(
                                        15.dp
                                    ),
                                color =
                                    Color.White.copy(
                                        alpha = 0.13f
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
                                            Color.White,
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
                                        data.plan_name
                                            ?.takeIf {
                                                it.isNotBlank()
                                            }
                                            ?: "Kế hoạch tháng ${data.month}, ${data.year}",
                                    color =
                                        Color.White,
                                    fontWeight =
                                        FontWeight.Bold,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleMedium
                                )

                                Text(
                                    text = dateRange,
                                    color =
                                        Color.White.copy(
                                            alpha = 0.72f
                                        ),
                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelSmall
                                )
                            }


                            Surface(
                                shape =
                                    RoundedCornerShape(
                                        50.dp
                                    ),
                                color =
                                    Color.White.copy(
                                        alpha = 0.14f
                                    )
                            ) {

                                Text(
                                    text =
                                        planStatus,
                                    modifier =
                                        Modifier.padding(
                                            horizontal = 9.dp,
                                            vertical = 5.dp
                                        ),
                                    color =
                                        Color.White,
                                    fontWeight =
                                        FontWeight.Bold,
                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelSmall
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    20.dp
                                )
                        )


                        /*
                        |--------------------------------------------------------------------------
                        | MONEY
                        |--------------------------------------------------------------------------
                        */

                        PlanTopMoneyRow(
                            title =
                                "Thu nhập dự kiến",
                            value =
                                "${formatter.format(data.monthly_income)} đ",
                            valueColor =
                                Color(
                                    0xFFA9F7C9
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    11.dp
                                )
                        )


                        PlanTopMoneyRow(
                            title =
                                "Ngân sách chi tiêu",
                            value =
                                "${formatter.format(totalExpense)} đ",
                            valueColor =
                                Color(
                                    0xFFFFB5C2
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    11.dp
                                )
                        )


                        HorizontalDivider(
                            color =
                                Color.White.copy(
                                    alpha = 0.18f
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    11.dp
                                )
                        )


                        PlanTopMoneyRow(
                            title =
                                "Còn lại",
                            value =
                                "${formatter.format(remaining)} đ",
                            valueColor =
                                Color(
                                    0xFFFFDA72
                                ),
                            bold = true
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
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            LinearProgressIndicator(
                                progress = {
                                    mainProgress
                                },
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(
                                            7.dp
                                        )
                                        .clip(
                                            RoundedCornerShape(
                                                20.dp
                                            )
                                        ),
                                color =
                                    Color.White,
                                trackColor =
                                    Color.White.copy(
                                        alpha = 0.18f
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
                                    "${formatDetailPercent(expensePercent)}%",
                                color =
                                    Color.White,
                                fontWeight =
                                    FontWeight.Bold,
                                style =
                                    MaterialTheme
                                        .typography
                                        .labelMedium
                            )
                        }
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | SAVING TARGET
        |--------------------------------------------------------------------------
        */

        item {

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
                                16.dp
                            ),
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
                            IncomeGreen.copy(
                                alpha = 0.10f
                            )
                    ) {

                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {

                            Text(
                                text = "₫",
                                color =
                                    IncomeGreen,
                                fontWeight =
                                    FontWeight.ExtraBold,
                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.width(
                                12.dp
                            )
                    )


                    Column(
                        modifier =
                            Modifier.weight(1f)
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

                        Spacer(
                            modifier =
                                Modifier.height(
                                    3.dp
                                )
                        )

                        Text(
                            text =
                                "${formatter.format(data.saving_target)} đ",
                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium,
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
        | ALLOCATION HEADER
        |--------------------------------------------------------------------------
        */

        item {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "Phân bổ chi tiêu",
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
                                2.dp
                            )
                    )

                    Text(
                        text =
                            "${data.allocations.size} danh mục được phân bổ",
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


                TextButton(
                    onClick = {
                        onAdjustClick(
                            planId
                        )
                    }
                ) {

                    Icon(
                        imageVector =
                            Icons.Default.Edit,
                        contentDescription =
                            null,
                        tint =
                            BrandPurple,
                        modifier =
                            Modifier.size(
                                18.dp
                            )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(
                                4.dp
                            )
                    )

                    Text(
                        text =
                            "Sửa",
                        color =
                            BrandPurple,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | EMPTY ALLOCATION
        |--------------------------------------------------------------------------
        */

        if (data.allocations.isEmpty()) {

            item {

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
                        )
                ) {

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    24.dp
                                ),
                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Surface(
                            modifier =
                                Modifier.size(
                                    58.dp
                                ),
                            shape =
                                CircleShape,
                            color =
                                BrandPurple.copy(
                                    alpha = 0.09f
                                )
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.PieChart,
                                    contentDescription =
                                        null,
                                    tint =
                                        BrandPurple,
                                    modifier =
                                        Modifier.size(
                                            28.dp
                                        )
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    12.dp
                                )
                        )


                        Text(
                            text =
                                "Chưa có phân bổ chi tiêu",
                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    5.dp
                                )
                        )


                        Text(
                            text =
                                "Điều chỉnh kế hoạch để phân bổ ngân sách cho từng danh mục.",
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }
                }
            }

        } else {


            /*
            |--------------------------------------------------------------------------
            | ALLOCATIONS
            |--------------------------------------------------------------------------
            */

            itemsIndexed(
                items =
                    data.allocations
            ) { index, allocation ->


                val percent =
                    if (data.flexible_budget > 0) {

                        (
                                allocation.planned_amount /
                                        data.flexible_budget *
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
                            percent /
                                    100.0
                            )
                        .toFloat()


                val itemColor =
                    allocationColor(
                        index
                    )


                Card(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            18.dp
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
                                    14.dp
                                ),
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
                                    42.dp
                                ),
                            shape =
                                RoundedCornerShape(
                                    13.dp
                                ),
                            color =
                                itemColor.copy(
                                    alpha = 0.11f
                                )
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Text(
                                    text =
                                        allocation
                                            .category_name
                                            .take(1)
                                            .uppercase(),
                                    color =
                                        itemColor,
                                    fontWeight =
                                        FontWeight.ExtraBold
                                )
                            }
                        }


                        Spacer(
                            modifier =
                                Modifier.width(
                                    11.dp
                                )
                        )


                        /*
                        |--------------------------------------------------------------------------
                        | INFO
                        |--------------------------------------------------------------------------
                        */

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Row(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Text(
                                    text =
                                        allocation.category_name,
                                    modifier =
                                        Modifier.weight(1f),
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium,
                                    fontWeight =
                                        FontWeight.Bold,
                                    maxLines = 1,
                                    overflow =
                                        TextOverflow.Ellipsis
                                )


                                Text(
                                    text =
                                        "${formatter.format(allocation.planned_amount)} đ",
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurface
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.height(
                                        8.dp
                                    )
                            )


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
                                            .height(
                                                6.dp
                                            )
                                            .clip(
                                                RoundedCornerShape(
                                                    20.dp
                                                )
                                            ),
                                    color =
                                        itemColor,
                                    trackColor =
                                        itemColor.copy(
                                            alpha = 0.10f
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
                                        "${formatDetailPercent(percent)}%",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .labelSmall,
                                    color =
                                        itemColor,
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
        | MESSAGE
        |--------------------------------------------------------------------------
        */

        if (!message.isNullOrBlank()) {

            item {

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            16.dp
                        ),
                    color =
                        BrandPurple.copy(
                            alpha = 0.08f
                        )
                ) {

                    Text(
                        text =
                            message ?: "",
                        modifier =
                            Modifier.padding(
                                14.dp
                            ),
                        color =
                            BrandPurple
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | ADJUST BUTTON
        |--------------------------------------------------------------------------
        */

        item {

            Spacer(
                modifier =
                    Modifier.height(
                        2.dp
                    )
            )


            Button(
                onClick = {

                    onAdjustClick(
                        planId
                    )
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            54.dp
                        ),
                shape =
                    RoundedCornerShape(
                        17.dp
                    ),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            BrandPurple
                    )
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Edit,
                    contentDescription =
                        null,
                    tint =
                        Color.White
                )


                Spacer(
                    modifier =
                        Modifier.width(
                            8.dp
                        )
                )


                Text(
                    text =
                        "Điều chỉnh kế hoạch",
                    color =
                        Color.White,
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| TOP MONEY ROW
|--------------------------------------------------------------------------
*/

@Composable
private fun PlanTopMoneyRow(
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
            color =
                Color.White.copy(
                    alpha = 0.72f
                ),
            style =
                MaterialTheme
                    .typography
                    .bodySmall
        )

        Text(
            text =
                value,
            color =
                valueColor,
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
| STATUS
|--------------------------------------------------------------------------
*/

private fun getDetailStatus(
    month: Int,
    year: Int
): String {

    val current =
        YearMonth.now()

    val target =
        try {

            YearMonth.of(
                year,
                month
            )

        } catch (_: Exception) {

            return ""
        }


    return when {

        target < current ->
            "Hoàn thành"

        target > current ->
            "Sắp tới"

        else ->
            "Đang thực hiện"
    }
}


/*
|--------------------------------------------------------------------------
| COLORS
|--------------------------------------------------------------------------
*/

private fun allocationColor(
    index: Int
): Color {

    val colors =
        listOf(
            Color(0xFFF05B6D),
            Color(0xFF438AF5),
            Color(0xFF27A96B),
            Color(0xFFF39B42),
            Color(0xFF7B5FEA),
            Color(0xFF18A8B5)
        )

    return colors[
        index % colors.size
    ]
}


/*
|--------------------------------------------------------------------------
| FORMAT PERCENT
|--------------------------------------------------------------------------
*/

private fun formatDetailPercent(
    value: Double
): String {

    return if (value % 1.0 == 0.0) {

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