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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinance.data.model.PlanAllocationRequest
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.viewmodel.PlanViewModel
import java.text.NumberFormat
import java.util.Locale


@Composable
fun AdjustPlanScreen(
    planId: Long,
    onBack: () -> Unit,
    planViewModel: PlanViewModel = viewModel()
) {

    val categories by
    planViewModel.expenseCategories.collectAsState()

    val plan by
    planViewModel.selectedPlan.collectAsState()

    val message by
    planViewModel.message.collectAsState()


    val formatter =
        remember {
            NumberFormat.getNumberInstance(
                Locale("vi", "VN")
            )
        }


    val allocationInputs =
        remember {
            mutableStateMapOf<Long, String>()
        }


    /*
    |--------------------------------------------------------------------------
    | LOAD
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(planId) {

        planViewModel.loadPlanDetail(
            planId
        )

        planViewModel.loadExpenseCategories()
    }


    /*
    |--------------------------------------------------------------------------
    | LOAD OLD ALLOCATION
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(
        plan,
        categories
    ) {

        val data =
            plan
                ?: return@LaunchedEffect

        if (categories.isEmpty()) {
            return@LaunchedEffect
        }

        allocationInputs.clear()


        categories.forEach { category ->

            val oldAllocation =
                data.allocations.find {
                    it.category_id ==
                            category.id
                }


            allocationInputs[
                category.id
            ] =
                oldAllocation
                    ?.planned_amount
                    ?.toLong()
                    ?.toString()
                    ?: ""
        }
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
                color =
                    BrandPurple
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

    val totalAllocated =
        allocationInputs
            .values
            .sumOf {

                it.toDoubleOrNull()
                    ?: 0.0
            }


    val remaining =
        data.flexible_budget -
                totalAllocated


    val allocationPercent =
        if (data.flexible_budget > 0) {

            (
                    totalAllocated /
                            data.flexible_budget *
                            100.0
                    )

        } else {

            0.0
        }


    val progress =
        (
                allocationPercent /
                        100.0
                )
            .toFloat()
            .coerceIn(
                0f,
                1f
            )


    val isOverBudget =
        remaining < 0


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
                bottom = 36.dp
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

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        TextButton(
                            onClick =
                                onBack,

                            contentPadding =
                                PaddingValues(
                                    0.dp
                                )
                        ) {

                            Text(
                                text = "‹",

                                style =
                                    MaterialTheme
                                        .typography
                                        .headlineMedium,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface
                            )
                        }
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
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            "Điều chỉnh kế hoạch",

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
                                2.dp
                            )
                    )


                    Text(
                        text =
                            "Phân bổ ngân sách cho từng danh mục",

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
        | SUMMARY
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

                        Text(
                            text =
                                "Ngân sách linh hoạt",

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
                                "${
                                    formatter.format(
                                        data.flexible_budget
                                    )
                                } đ",

                            color =
                                Color.White,

                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium,

                            fontWeight =
                                FontWeight.ExtraBold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    16.dp
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

                            AdjustSummaryMiniCard(
                                title =
                                    "Đã phân bổ",

                                value =
                                    "${
                                        formatter.format(
                                            totalAllocated
                                        )
                                    } đ",

                                valueColor =
                                    Color(
                                        0xFFFFD66C
                                    ),

                                modifier =
                                    Modifier.weight(
                                        1f
                                    )
                            )


                            AdjustSummaryMiniCard(
                                title =
                                    if (isOverBudget) {
                                        "Vượt quá"
                                    } else {
                                        "Còn lại"
                                    },

                                value =
                                    "${
                                        formatter.format(
                                            kotlin.math.abs(
                                                remaining
                                            )
                                        )
                                    } đ",

                                valueColor =
                                    if (isOverBudget) {
                                        Color(
                                            0xFFFFB2C0
                                        )
                                    } else {
                                        Color(
                                            0xFFA7F4C7
                                        )
                                    },

                                modifier =
                                    Modifier.weight(
                                        1f
                                    )
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    14.dp
                                )
                        )


                        LinearProgressIndicator(
                            progress = {
                                progress
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(
                                        7.dp
                                    )
                                    .clip(
                                        RoundedCornerShape(
                                            20.dp
                                        )
                                    ),

                            color =
                                if (isOverBudget) {

                                    Color(
                                        0xFFFFB2C0
                                    )

                                } else {

                                    Color(
                                        0xFFA7F4C7
                                    )
                                },

                            trackColor =
                                Color.White.copy(
                                    alpha = 0.18f
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    7.dp
                                )
                        )


                        Text(
                            text =
                                "${
                                    formatAdjustPercent(
                                        allocationPercent
                                    )
                                }% ngân sách đã được phân bổ",

                            style =
                                MaterialTheme
                                    .typography
                                    .labelSmall,

                            color =
                                Color.White.copy(
                                    alpha = 0.75f
                                )
                        )
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | TITLE
        |--------------------------------------------------------------------------
        */

        item {

            Column {

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
                            3.dp
                        )
                )


                Text(
                    text =
                        "Nhập số tiền dự kiến cho từng danh mục",

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
        | EMPTY CATEGORY
        |--------------------------------------------------------------------------
        */

        if (categories.isEmpty()) {

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

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    28.dp
                                ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Text(
                            text =
                                "Chưa có danh mục chi tiêu.",

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
            }

        } else {


            /*
            |--------------------------------------------------------------------------
            | CATEGORY
            |--------------------------------------------------------------------------
            */

            itemsIndexed(
                items =
                    categories,

                key = { _, category ->
                    category.id
                }

            ) { index, category ->


                val amount =
                    allocationInputs[
                        category.id
                    ]
                        ?.toDoubleOrNull()
                        ?: 0.0


                val itemPercent =
                    if (
                        data.flexible_budget > 0
                    ) {

                        (
                                amount /
                                        data.flexible_budget *
                                        100.0
                                )

                    } else {

                        0.0
                    }


                val itemProgress =
                    (
                            itemPercent /
                                    100.0
                            )
                        .toFloat()
                        .coerceIn(
                            0f,
                            1f
                        )


                val itemColor =
                    adjustCategoryColor(
                        index
                    )


                Card(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            19.dp
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

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    14.dp
                                )
                    ) {


                        /*
                        |--------------------------------------------------------------------------
                        | HEADER CATEGORY
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
                                            category.name
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


                            Column(
                                modifier =
                                    Modifier.weight(
                                        1f
                                    )
                            ) {

                                Text(
                                    text =
                                        category.name,

                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium,

                                    fontWeight =
                                        FontWeight.Bold,

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
                                    text =
                                        "${
                                            formatAdjustPercent(
                                                itemPercent
                                            )
                                        }% ngân sách linh hoạt",

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
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    12.dp
                                )
                        )


                        /*
                        |--------------------------------------------------------------------------
                        | INPUT
                        |--------------------------------------------------------------------------
                        */

                        OutlinedTextField(
                            value =
                                allocationInputs[
                                    category.id
                                ]
                                    ?: "",

                            onValueChange = { value ->

                                /*
                                 * VND nên chỉ cho nhập số nguyên.
                                 */
                                val filtered =
                                    value.filter {
                                        it.isDigit()
                                    }


                                allocationInputs[
                                    category.id
                                ] =
                                    filtered
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            label = {

                                Text(
                                    "Số tiền phân bổ"
                                )
                            },

                            placeholder = {

                                Text(
                                    "0"
                                )
                            },

                            suffix = {

                                Text(
                                    "đ"
                                )
                            },

                            singleLine =
                                true,

                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType =
                                        KeyboardType.Number
                                ),

                            shape =
                                RoundedCornerShape(
                                    14.dp
                                )
                        )


                        /*
                        |--------------------------------------------------------------------------
                        | PROGRESS CATEGORY
                        |--------------------------------------------------------------------------
                        */

                        if (amount > 0) {

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        10.dp
                                    )
                            )


                            LinearProgressIndicator(
                                progress = {
                                    itemProgress
                                },

                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(
                                            5.dp
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
                        }
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | REMAINING / WARNING
        |--------------------------------------------------------------------------
        */

        item {

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(
                        17.dp
                    ),

                color =
                    if (isOverBudget) {

                        ExpenseRed.copy(
                            alpha = 0.09f
                        )

                    } else {

                        IncomeGreen.copy(
                            alpha = 0.09f
                        )
                    }
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            15.dp
                        )
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
                                if (isOverBudget) {
                                    "Vượt ngân sách"
                                } else {
                                    "Còn có thể phân bổ"
                                },

                            color =
                                if (isOverBudget) {
                                    ExpenseRed
                                } else {
                                    IncomeGreen
                                },

                            fontWeight =
                                FontWeight.Bold
                        )


                        Text(
                            text =
                                "${
                                    formatter.format(
                                        kotlin.math.abs(
                                            remaining
                                        )
                                    )
                                } đ",

                            color =
                                if (isOverBudget) {
                                    ExpenseRed
                                } else {
                                    IncomeGreen
                                },

                            fontWeight =
                                FontWeight.ExtraBold
                        )
                    }


                    if (isOverBudget) {

                        Spacer(
                            modifier =
                                Modifier.height(
                                    5.dp
                                )
                        )


                        Text(
                            text =
                                "Hãy giảm phân bổ ở một hoặc nhiều danh mục trước khi lưu.",

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
                            BrandPurple,

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | SAVE
        |--------------------------------------------------------------------------
        */

        item {

            Button(
                onClick = {

                    if (
                        isOverBudget ||
                        categories.isEmpty()
                    ) {
                        return@Button
                    }


                    val allocations =
                        categories.map { category ->

                            val amount =
                                allocationInputs[
                                    category.id
                                ]
                                    ?.toDoubleOrNull()
                                    ?: 0.0


                            PlanAllocationRequest(
                                category_id =
                                    category.id,

                                planned_amount =
                                    amount,

                                note =
                                    null
                            )
                        }


                    planViewModel.adjustPlan(

                        planId =
                            planId,

                        allocations =
                            allocations,

                        onSuccess = {

                            onBack()
                        }
                    )
                },

                enabled =
                    categories.isNotEmpty() &&
                            !isOverBudget,

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
                            BrandPurple,

                        disabledContainerColor =
                            BrandPurple.copy(
                                alpha = 0.35f
                            )
                    )
            ) {

                Text(
                    text =
                        "Lưu phân bổ",

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        Color.White
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| SUMMARY MINI
|--------------------------------------------------------------------------
*/

@Composable
private fun AdjustSummaryMiniCard(
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
                16.dp
            ),

        color =
            Color.White.copy(
                alpha = 0.10f
            )
    ) {

        Column(
            modifier =
                Modifier.padding(
                    12.dp
                )
        ) {

            Text(
                text =
                    title,

                style =
                    MaterialTheme
                        .typography
                        .labelSmall,

                color =
                    Color.White.copy(
                        alpha = 0.72f
                    )
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

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,

                fontWeight =
                    FontWeight.ExtraBold
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| CATEGORY COLORS
|--------------------------------------------------------------------------
*/

private fun adjustCategoryColor(
    index: Int
): Color {

    val colors =
        listOf(
            Color(0xFFF05B6D),
            Color(0xFF438AF5),
            Color(0xFF27A96B),
            Color(0xFFF39B42),
            Color(0xFF7B5FEA),
            Color(0xFF18A8B5),
            Color(0xFFE06DB1),
            Color(0xFF607D8B)
        )


    return colors[
        index % colors.size
    ]
}


/*
|--------------------------------------------------------------------------
| FORMAT
|--------------------------------------------------------------------------
*/

private fun formatAdjustPercent(
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