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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.ui.theme.BrandGold
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.viewmodel.PlanViewModel

import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun AddPlanScreen(
    onBack: () -> Unit,
    planViewModel: PlanViewModel = viewModel()
) {

    val calendar =
        Calendar.getInstance()

    val formatter =
        remember {
            NumberFormat.getNumberInstance(
                Locale("vi", "VN")
            )
        }


    /*
    |--------------------------------------------------------------------------
    | INPUT
    |--------------------------------------------------------------------------
    */

    var planName by remember {
        mutableStateOf("")
    }

    var monthlyIncome by remember {
        mutableStateOf("")
    }

    var fixedExpense by remember {
        mutableStateOf("")
    }

    var savingTarget by remember {
        mutableStateOf("")
    }


    /*
    |--------------------------------------------------------------------------
    | MONTH / YEAR
    |--------------------------------------------------------------------------
    |
    | Dùng String để người dùng có thể xóa và nhập lại.
    |--------------------------------------------------------------------------
    */

    var monthInput by remember {

        mutableStateOf(
            (
                    calendar.get(
                        Calendar.MONTH
                    ) + 1
                    ).toString()
        )
    }


    var yearInput by remember {

        mutableStateOf(
            calendar.get(
                Calendar.YEAR
            ).toString()
        )
    }


    var localMessage by remember {
        mutableStateOf<String?>(null)
    }


    /*
    |--------------------------------------------------------------------------
    | VALUES
    |--------------------------------------------------------------------------
    */

    val incomeValue =
        monthlyIncome
            .toDoubleOrNull()
            ?: 0.0


    val fixedValue =
        fixedExpense
            .toDoubleOrNull()
            ?: 0.0


    val savingValue =
        savingTarget
            .toDoubleOrNull()
            ?: 0.0


    val month =
        monthInput
            .toIntOrNull()
            ?: 0


    val year =
        yearInput
            .toIntOrNull()
            ?: 0


    /*
    |--------------------------------------------------------------------------
    | FLEXIBLE BUDGET
    |--------------------------------------------------------------------------
    */

    val flexibleBudget =
        incomeValue -
                fixedValue -
                savingValue


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


                /*
                |--------------------------------------------------------------------------
                | BACK
                |--------------------------------------------------------------------------
                */

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
                                text =
                                    "‹",

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
                            "Tạo kế hoạch tài chính",

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
                            "Lập kế hoạch thu nhập, chi tiêu và tiết kiệm",

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
        | PREVIEW CARD
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
                                "Ngân sách linh hoạt dự kiến",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                Color.White.copy(
                                    alpha = 0.72f
                                )
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    5.dp
                                )
                        )


                        Text(
                            text =
                                "${
                                    formatter.format(
                                        flexibleBudget
                                    )
                                } đ",

                            style =
                                MaterialTheme
                                    .typography
                                    .headlineMedium,

                            fontWeight =
                                FontWeight.ExtraBold,

                            color =
                                if (
                                    flexibleBudget >= 0
                                ) {

                                    Color.White

                                } else {

                                    Color(
                                        0xFFFFB1BF
                                    )
                                }
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

                            AddPlanMiniCard(
                                title =
                                    "Thu nhập",

                                value =
                                    "${
                                        formatter.format(
                                            incomeValue
                                        )
                                    } đ",

                                valueColor =
                                    Color(
                                        0xFFA7F4C7
                                    ),

                                modifier =
                                    Modifier.weight(
                                        1f
                                    )
                            )


                            AddPlanMiniCard(
                                title =
                                    "Chi cố định",

                                value =
                                    "${
                                        formatter.format(
                                            fixedValue
                                        )
                                    } đ",

                                valueColor =
                                    Color(
                                        0xFFFFB1BF
                                    ),

                                modifier =
                                    Modifier.weight(
                                        1f
                                    )
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(
                                    10.dp
                                )
                        )


                        AddPlanMiniCard(
                            title =
                                "Mục tiêu tiết kiệm",

                            value =
                                "${
                                    formatter.format(
                                        savingValue
                                    )
                                } đ",

                            valueColor =
                                Color(
                                    0xFFFFD66C
                                ),

                            modifier =
                                Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | THÔNG TIN KẾ HOẠCH
        |--------------------------------------------------------------------------
        */

        item {

            Text(
                text =
                    "Thông tin kế hoạch",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.ExtraBold
            )
        }


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
                    | NAME
                    |--------------------------------------------------------------------------
                    */

                    OutlinedTextField(
                        value =
                            planName,

                        onValueChange = {
                            planName = it
                            localMessage = null
                        },

                        label = {
                            Text(
                                "Tên kế hoạch"
                            )
                        },

                        placeholder = {
                            Text(
                                "Ví dụ: Kế hoạch tháng 9"
                            )
                        },

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                14.dp
                            ),

                        modifier =
                            Modifier.fillMaxWidth()
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | MONTH + YEAR
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

                        OutlinedTextField(
                            value =
                                monthInput,

                            onValueChange = { value ->

                                monthInput =
                                    value
                                        .filter {
                                            it.isDigit()
                                        }
                                        .take(
                                            2
                                        )

                                localMessage = null
                            },

                            label = {
                                Text(
                                    "Tháng"
                                )
                            },

                            placeholder = {
                                Text(
                                    "1 - 12"
                                )
                            },

                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType =
                                        KeyboardType.Number
                                ),

                            singleLine =
                                true,

                            shape =
                                RoundedCornerShape(
                                    14.dp
                                ),

                            modifier =
                                Modifier.weight(
                                    1f
                                )
                        )


                        OutlinedTextField(
                            value =
                                yearInput,

                            onValueChange = { value ->

                                yearInput =
                                    value
                                        .filter {
                                            it.isDigit()
                                        }
                                        .take(
                                            4
                                        )

                                localMessage = null
                            },

                            label = {
                                Text(
                                    "Năm"
                                )
                            },

                            placeholder = {
                                Text(
                                    "2026"
                                )
                            },

                            keyboardOptions =
                                KeyboardOptions(
                                    keyboardType =
                                        KeyboardType.Number
                                ),

                            singleLine =
                                true,

                            shape =
                                RoundedCornerShape(
                                    14.dp
                                ),

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
        | THÔNG TIN TÀI CHÍNH
        |--------------------------------------------------------------------------
        */

        item {

            Text(
                text =
                    "Thông tin tài chính",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.ExtraBold
            )
        }


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
                    | INCOME
                    |--------------------------------------------------------------------------
                    */

                    MoneyTitle(
                        title =
                            "Thu nhập dự kiến",

                        color =
                            IncomeGreen
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                7.dp
                            )
                    )


                    OutlinedTextField(
                        value =
                            monthlyIncome,

                        onValueChange = { value ->

                            monthlyIncome =
                                filterMoneyInput(
                                    value
                                )

                            localMessage = null
                        },

                        label = {
                            Text(
                                "Số tiền"
                            )
                        },

                        suffix = {
                            Text(
                                "đ"
                            )
                        },

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            ),

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                14.dp
                            ),

                        modifier =
                            Modifier.fillMaxWidth()
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                16.dp
                            )
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | FIXED EXPENSE
                    |--------------------------------------------------------------------------
                    */

                    MoneyTitle(
                        title =
                            "Chi phí cố định",

                        color =
                            ExpenseRed
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                7.dp
                            )
                    )


                    OutlinedTextField(
                        value =
                            fixedExpense,

                        onValueChange = { value ->

                            fixedExpense =
                                filterMoneyInput(
                                    value
                                )

                            localMessage = null
                        },

                        label = {
                            Text(
                                "Số tiền"
                            )
                        },

                        suffix = {
                            Text(
                                "đ"
                            )
                        },

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            ),

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                14.dp
                            ),

                        modifier =
                            Modifier.fillMaxWidth()
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                16.dp
                            )
                    )


                    /*
                    |--------------------------------------------------------------------------
                    | SAVING
                    |--------------------------------------------------------------------------
                    */

                    MoneyTitle(
                        title =
                            "Mục tiêu tiết kiệm",

                        color =
                            BrandGold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                7.dp
                            )
                    )


                    OutlinedTextField(
                        value =
                            savingTarget,

                        onValueChange = { value ->

                            savingTarget =
                                filterMoneyInput(
                                    value
                                )

                            localMessage = null
                        },

                        label = {
                            Text(
                                "Số tiền"
                            )
                        },

                        suffix = {
                            Text(
                                "đ"
                            )
                        },

                        keyboardOptions =
                            KeyboardOptions(
                                keyboardType =
                                    KeyboardType.Number
                            ),

                        singleLine =
                            true,

                        shape =
                            RoundedCornerShape(
                                14.dp
                            ),

                        modifier =
                            Modifier.fillMaxWidth()
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | PREVIEW RESULT
        |--------------------------------------------------------------------------
        */

        if (
            incomeValue > 0
        ) {

            item {

                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(
                            17.dp
                        ),

                    color =
                        if (
                            flexibleBudget >= 0
                        ) {

                            IncomeGreen.copy(
                                alpha = 0.09f
                            )

                        } else {

                            ExpenseRed.copy(
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
                                    if (
                                        flexibleBudget >= 0
                                    ) {

                                        "Có thể phân bổ"

                                    } else {

                                        "Vượt thu nhập"
                                    },

                                color =
                                    if (
                                        flexibleBudget >= 0
                                    ) {

                                        IncomeGreen

                                    } else {

                                        ExpenseRed
                                    },

                                fontWeight =
                                    FontWeight.Bold
                            )


                            Text(
                                text =
                                    "${
                                        formatter.format(
                                            kotlin.math.abs(
                                                flexibleBudget
                                            )
                                        )
                                    } đ",

                                color =
                                    if (
                                        flexibleBudget >= 0
                                    ) {

                                        IncomeGreen

                                    } else {

                                        ExpenseRed
                                    },

                                fontWeight =
                                    FontWeight.ExtraBold
                            )
                        }


                        if (
                            flexibleBudget < 0
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        5.dp
                                    )
                            )


                            Text(
                                text =
                                    "Chi phí cố định và mục tiêu tiết kiệm đang lớn hơn thu nhập.",

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
        }


        /*
        |--------------------------------------------------------------------------
        | ERROR MESSAGE
        |--------------------------------------------------------------------------
        */

        if (
            !localMessage.isNullOrBlank()
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
                        ExpenseRed.copy(
                            alpha = 0.09f
                        )
                ) {

                    Text(
                        text =
                            localMessage
                                ?: "",

                        modifier =
                            Modifier.padding(
                                14.dp
                            ),

                        color =
                            ExpenseRed,

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | CREATE
        |--------------------------------------------------------------------------
        */

        item {

            Button(
                onClick = {

                    localMessage =
                        validatePlan(
                            monthlyIncome =
                                monthlyIncome,

                            fixedExpense =
                                fixedExpense,

                            savingTarget =
                                savingTarget,

                            incomeValue =
                                incomeValue,

                            fixedValue =
                                fixedValue,

                            savingValue =
                                savingValue,

                            flexibleBudget =
                                flexibleBudget,

                            month =
                                month,

                            year =
                                year
                        )


                    if (
                        localMessage != null
                    ) {

                        return@Button
                    }


                    planViewModel.createPlan(

                        planName =
                            planName
                                .trim()
                                .ifBlank {
                                    null
                                },

                        monthlyIncome =
                            incomeValue,

                        fixedExpense =
                            fixedValue,

                        savingTarget =
                            savingValue,

                        month =
                            month,

                        year =
                            year,

                        onSuccess = {

                            onBack()
                        }
                    )
                },

                enabled =
                    flexibleBudget >= 0,

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
                        "Tạo kế hoạch",

                    color =
                        Color.White,

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | CANCEL
        |--------------------------------------------------------------------------
        */

        item {

            TextButton(
                onClick =
                    onBack,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Hủy",

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,

                    fontWeight =
                        FontWeight.SemiBold
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| MONEY TITLE
|--------------------------------------------------------------------------
*/

@Composable
private fun MoneyTitle(
    title: String,
    color: Color
) {

    Text(
        text =
            title,

        style =
            MaterialTheme
                .typography
                .titleSmall,

        color =
            color,

        fontWeight =
            FontWeight.Bold
    )
}


/*
|--------------------------------------------------------------------------
| MINI CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun AddPlanMiniCard(
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
| FILTER MONEY
|--------------------------------------------------------------------------
*/

private fun filterMoneyInput(
    value: String
): String {

    return value.filter {
        it.isDigit()
    }
}


/*
|--------------------------------------------------------------------------
| VALIDATE
|--------------------------------------------------------------------------
*/

private fun validatePlan(
    monthlyIncome: String,
    fixedExpense: String,
    savingTarget: String,
    incomeValue: Double,
    fixedValue: Double,
    savingValue: Double,
    flexibleBudget: Double,
    month: Int,
    year: Int
): String? {

    if (
        monthlyIncome.isBlank()
    ) {

        return "Vui lòng nhập thu nhập dự kiến."
    }


    if (
        fixedExpense.isBlank()
    ) {

        return "Vui lòng nhập chi phí cố định."
    }


    if (
        savingTarget.isBlank()
    ) {

        return "Vui lòng nhập mục tiêu tiết kiệm."
    }


    if (
        incomeValue <= 0
    ) {

        return "Thu nhập dự kiến phải lớn hơn 0."
    }


    if (
        fixedValue < 0 ||
        savingValue < 0
    ) {

        return "Các số tiền không được âm."
    }


    if (
        month !in 1..12
    ) {

        return "Tháng phải nằm trong khoảng từ 1 đến 12."
    }


    if (
        year !in 2000..2100
    ) {

        return "Năm không hợp lệ."
    }


    if (
        flexibleBudget < 0
    ) {

        return "Chi phí cố định và mục tiêu tiết kiệm đang vượt quá thu nhập dự kiến."
    }


    return null
}