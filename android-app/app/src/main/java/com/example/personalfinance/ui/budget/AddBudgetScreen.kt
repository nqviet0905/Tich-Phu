package com.example.personalfinance.ui.budget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Flag

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.data.model.Category
import com.example.personalfinance.data.repository.CategoryRepository
import com.example.personalfinance.viewmodel.BudgetViewModel

import kotlinx.coroutines.launch

import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    onBack: () -> Unit,
    budgetViewModel: BudgetViewModel = viewModel()
) {

    /*
    |--------------------------------------------------------------------------
    | CURRENT DATE
    |--------------------------------------------------------------------------
    */

    val calendar =
        remember {
            Calendar.getInstance()
        }

    val currentMonth =
        calendar.get(
            Calendar.MONTH
        ) + 1

    val currentYear =
        calendar.get(
            Calendar.YEAR
        )


    /*
    |--------------------------------------------------------------------------
    | FORMATTER
    |--------------------------------------------------------------------------
    */

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
    | STATE
    |--------------------------------------------------------------------------
    */

    var categoryId by remember {
        mutableStateOf<Long?>(
            null
        )
    }

    var amount by remember {
        mutableStateOf("")
    }

    var month by remember {
        mutableStateOf(
            currentMonth
        )
    }

    var yearText by remember {
        mutableStateOf(
            currentYear.toString()
        )
    }

    var warningLevel by remember {
        mutableStateOf(
            "90"
        )
    }

    var categoryExpanded by remember {
        mutableStateOf(
            false
        )
    }

    var monthExpanded by remember {
        mutableStateOf(
            false
        )
    }

    var categories by remember {
        mutableStateOf(
            emptyList<Category>()
        )
    }

    var isLoadingCategories by remember {
        mutableStateOf(
            true
        )
    }

    var errorMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }


    /*
    |--------------------------------------------------------------------------
    | REPOSITORY
    |--------------------------------------------------------------------------
    */

    val repository =
        remember {
            CategoryRepository()
        }

    val scope =
        rememberCoroutineScope()


    val message by
    budgetViewModel.message
        .collectAsState()


    /*
    |--------------------------------------------------------------------------
    | LOAD EXPENSE CATEGORIES
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(Unit) {

        isLoadingCategories =
            true

        scope.launch {

            val result =
                repository.getCategories(
                    "expense"
                )

            result.onSuccess {

                categories =
                    it

                isLoadingCategories =
                    false
            }

            result.onFailure {

                errorMessage =
                    it.message
                        ?: "Không thể tải danh mục chi."

                isLoadingCategories =
                    false
            }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | SELECTED CATEGORY
    |--------------------------------------------------------------------------
    */

    val selectedCategory =
        categories.find {
            it.id ==
                    categoryId
        }


    /*
    |--------------------------------------------------------------------------
    | MONEY VALUE
    |--------------------------------------------------------------------------
    */

    val amountValue =
        amount.toDoubleOrNull()
            ?: 0.0


    /*
    |--------------------------------------------------------------------------
    | SCREEN
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
                )
                .imePadding(),
        contentPadding =
            PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 16.dp,
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
                        Modifier
                            .weight(
                                1f
                            )
                            .padding(
                                start = 14.dp
                            )
                ) {

                    Text(
                        text =
                            "Tạo ngân sách",
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
                                3.dp
                            )
                    )


                    Text(
                        text =
                            "Đặt giới hạn chi tiêu cho từng danh mục",
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
        | PREVIEW
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
                                .primaryContainer
                    )
            ) {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                18.dp
                            ),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Surface(
                        modifier =
                            Modifier.size(
                                52.dp
                            ),
                        shape =
                            RoundedCornerShape(
                                17.dp
                            ),
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                                .copy(
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
                                    MaterialTheme
                                        .colorScheme
                                        .primary,
                                modifier =
                                    Modifier.size(
                                        27.dp
                                    )
                            )
                        }
                    }


                    Column(
                        modifier =
                            Modifier
                                .weight(
                                    1f
                                )
                                .padding(
                                    start = 14.dp
                                )
                    ) {

                        Text(
                            text =
                                selectedCategory
                                    ?.name
                                    ?: "Hạn mức ngân sách",
                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,
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
                                if (
                                    amountValue > 0
                                ) {

                                    "${
                                        formatter.format(
                                            amountValue
                                        )
                                    } đ"

                                } else {

                                    "0 đ"
                                },
                            style =
                                MaterialTheme
                                    .typography
                                    .titleLarge,
                            fontWeight =
                                FontWeight.ExtraBold,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )


                        Spacer(
                            modifier =
                                Modifier.height(
                                    3.dp
                                )
                        )


                        Text(
                            text =
                                "Tháng $month/${yearText.ifBlank { currentYear.toString() }}",
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
        | CATEGORY + AMOUNT
        |--------------------------------------------------------------------------
        */

        item {

            AddBudgetFormCard(
                title =
                    "Thông tin ngân sách",
                icon =
                    Icons.Default
                        .AccountBalanceWallet
            ) {


                /*
                |--------------------------------------------------------------------------
                | CATEGORY
                |--------------------------------------------------------------------------
                */

                ExposedDropdownMenuBox(
                    expanded =
                        categoryExpanded,
                    onExpandedChange = {

                        if (
                            !isLoadingCategories
                        ) {

                            categoryExpanded =
                                !categoryExpanded
                        }
                    }
                ) {

                    OutlinedTextField(
                        value =
                            when {

                                isLoadingCategories ->
                                    "Đang tải danh mục..."

                                selectedCategory != null ->
                                    selectedCategory.name

                                else ->
                                    "Chọn danh mục"
                            },
                        onValueChange = {},
                        readOnly =
                            true,
                        label = {

                            Text(
                                "Danh mục chi"
                            )
                        },
                        leadingIcon = {

                            if (
                                isLoadingCategories
                            ) {

                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.size(
                                            20.dp
                                        ),
                                    strokeWidth =
                                        2.dp
                                )

                            } else {

                                Icon(
                                    imageVector =
                                        Icons.Default
                                            .AccountBalanceWallet,
                                    contentDescription =
                                        null
                                )
                            }
                        },
                        trailingIcon = {

                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded =
                                        categoryExpanded
                                )
                        },
                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )


                    ExposedDropdownMenu(
                        expanded =
                            categoryExpanded,
                        onDismissRequest = {

                            categoryExpanded =
                                false
                        }
                    ) {

                        if (
                            categories.isEmpty()
                        ) {

                            DropdownMenuItem(
                                text = {

                                    Text(
                                        "Không có danh mục chi"
                                    )
                                },
                                onClick = {}
                            )

                        } else {

                            categories.forEach {
                                    category ->

                                DropdownMenuItem(
                                    text = {

                                        Text(
                                            category.name
                                        )
                                    },
                                    onClick = {

                                        categoryId =
                                            category.id

                                        categoryExpanded =
                                            false

                                        errorMessage =
                                            null
                                    }
                                )
                            }
                        }
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
                | AMOUNT
                |--------------------------------------------------------------------------
                */

                OutlinedTextField(
                    value =
                        amount,
                    onValueChange = {
                            value ->

                        amount =
                            value.filter {
                                it.isDigit()
                            }

                        errorMessage =
                            null
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {

                        Text(
                            "Số tiền ngân sách"
                        )
                    },
                    placeholder = {

                        Text(
                            "Ví dụ: 3000000"
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default
                                    .AccountBalanceWallet,
                            contentDescription =
                                null
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
                            16.dp
                        )
                )


                if (
                    amountValue > 0
                ) {

                    Text(
                        text =
                            "${
                                formatter.format(
                                    amountValue
                                )
                            } đ",
                        modifier =
                            Modifier.padding(
                                start = 4.dp,
                                top = 5.dp
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .primary,
                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }
        }


        /*
        |--------------------------------------------------------------------------
        | TIME
        |--------------------------------------------------------------------------
        */

        item {

            AddBudgetFormCard(
                title =
                    "Thời gian áp dụng",
                icon =
                    Icons.Default
                        .CalendarMonth
            ) {


                /*
                |--------------------------------------------------------------------------
                | MONTH
                |--------------------------------------------------------------------------
                */

                ExposedDropdownMenuBox(
                    expanded =
                        monthExpanded,
                    onExpandedChange = {

                        monthExpanded =
                            !monthExpanded
                    }
                ) {

                    OutlinedTextField(
                        value =
                            "Tháng $month",
                        onValueChange = {},
                        readOnly =
                            true,
                        label = {

                            Text(
                                "Tháng"
                            )
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default
                                        .CalendarMonth,
                                contentDescription =
                                    null
                            )
                        },
                        trailingIcon = {

                            ExposedDropdownMenuDefaults
                                .TrailingIcon(
                                    expanded =
                                        monthExpanded
                                )
                        },
                        modifier =
                            Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )


                    ExposedDropdownMenu(
                        expanded =
                            monthExpanded,
                        onDismissRequest = {

                            monthExpanded =
                                false
                        }
                    ) {

                        for (
                        m in 1..12
                        ) {

                            DropdownMenuItem(
                                text = {

                                    Text(
                                        "Tháng $m"
                                    )
                                },
                                onClick = {

                                    month =
                                        m

                                    monthExpanded =
                                        false

                                    errorMessage =
                                        null
                                }
                            )
                        }
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
                | YEAR
                |--------------------------------------------------------------------------
                */

                OutlinedTextField(
                    value =
                        yearText,
                    onValueChange = {
                            value ->

                        yearText =
                            value
                                .filter {
                                    it.isDigit()
                                }
                                .take(
                                    4
                                )

                        errorMessage =
                            null
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {

                        Text(
                            "Năm"
                        )
                    },
                    placeholder = {

                        Text(
                            currentYear.toString()
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default
                                    .CalendarMonth,
                            contentDescription =
                                null
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
                            16.dp
                        )
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | WARNING
        |--------------------------------------------------------------------------
        */

        item {

            AddBudgetFormCard(
                title =
                    "Cảnh báo chi tiêu",
                icon =
                    Icons.Default.Flag
            ) {

                OutlinedTextField(
                    value =
                        warningLevel,
                    onValueChange = {
                            value ->

                        warningLevel =
                            value
                                .filter {
                                    it.isDigit()
                                }
                                .take(
                                    3
                                )

                        errorMessage =
                            null
                    },
                    modifier =
                        Modifier.fillMaxWidth(),
                    label = {

                        Text(
                            "Mức cảnh báo"
                        )
                    },
                    placeholder = {

                        Text(
                            "90"
                        )
                    },
                    leadingIcon = {

                        Icon(
                            imageVector =
                                Icons.Default.Flag,
                            contentDescription =
                                null
                        )
                    },
                    suffix = {

                        Text(
                            "%"
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
                            16.dp
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            10.dp
                        )
                )


                Surface(
                    modifier =
                        Modifier.fillMaxWidth(),
                    shape =
                        RoundedCornerShape(
                            15.dp
                        ),
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                            .copy(
                                alpha = 0.07f
                            )
                ) {

                    Text(
                        text =
                            buildWarningDescription(
                                warningLevel
                            ),
                        modifier =
                            Modifier.padding(
                                13.dp
                            ),
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
        | ERROR
        |--------------------------------------------------------------------------
        */

        if (
            !errorMessage.isNullOrBlank()
        ) {

            item {

                AddBudgetMessageCard(
                    text =
                        errorMessage
                            ?: ""
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | SERVER MESSAGE
        |--------------------------------------------------------------------------
        */

        if (
            !message.isNullOrBlank()
        ) {

            item {

                AddBudgetMessageCard(
                    text =
                        message
                            ?: ""
                )
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

                    errorMessage =
                        null


                    val category =
                        categoryId

                    val amountParsed =
                        amount
                            .toDoubleOrNull()

                    val yearParsed =
                        yearText
                            .toIntOrNull()

                    val warningParsed =
                        warningLevel
                            .toIntOrNull()


                    /*
                    |--------------------------------------------------------------------------
                    | CATEGORY
                    |--------------------------------------------------------------------------
                    */

                    if (
                        category == null
                    ) {

                        errorMessage =
                            "Vui lòng chọn danh mục chi."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | AMOUNT
                    |--------------------------------------------------------------------------
                    */

                    if (
                        amountParsed == null ||
                        amountParsed <= 0
                    ) {

                        errorMessage =
                            "Số tiền ngân sách phải lớn hơn 0."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | MONTH
                    |--------------------------------------------------------------------------
                    */

                    if (
                        month !in 1..12
                    ) {

                        errorMessage =
                            "Tháng không hợp lệ."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | YEAR
                    |--------------------------------------------------------------------------
                    */

                    if (
                        yearParsed == null ||
                        yearParsed !in 2000..2100
                    ) {

                        errorMessage =
                            "Năm phải nằm trong khoảng 2000 đến 2100."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | WARNING
                    |--------------------------------------------------------------------------
                    */

                    if (
                        warningParsed == null ||
                        warningParsed !in 1..100
                    ) {

                        errorMessage =
                            "Mức cảnh báo phải từ 1 đến 100%."

                        return@Button
                    }


                    /*
                    |--------------------------------------------------------------------------
                    | CREATE
                    |--------------------------------------------------------------------------
                    */

                    budgetViewModel
                        .createBudget(
                            categoryId =
                                category,
                            amount =
                                amountParsed,
                            month =
                                month,
                            year =
                                yearParsed,
                            warningLevel =
                                warningParsed,
                            onSuccess = {

                                onBack()
                            }
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
                        "Tạo ngân sách",
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        4.dp
                    )
            )


            TextButton(
                onClick =
                    onBack,
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Hủy",
                    fontWeight =
                        FontWeight.SemiBold,
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
| FORM CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun AddBudgetFormCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
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
                        16.dp
                    )
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Surface(
                    modifier =
                        Modifier.size(
                            38.dp
                        ),
                    shape =
                        RoundedCornerShape(
                            12.dp
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
                                icon,
                            contentDescription =
                                null,
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary,
                            modifier =
                                Modifier.size(
                                    21.dp
                                )
                        )
                    }
                }


                Text(
                    text =
                        title,
                    modifier =
                        Modifier.padding(
                            start = 10.dp
                        ),
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,
                    fontWeight =
                        FontWeight.Bold
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        15.dp
                    )
            )


            content()
        }
    }
}


/*
|--------------------------------------------------------------------------
| MESSAGE
|--------------------------------------------------------------------------
*/

@Composable
private fun AddBudgetMessageCard(
    text: String
) {

    Surface(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                15.dp
            ),
        color =
            MaterialTheme
                .colorScheme
                .errorContainer
    ) {

        Text(
            text =
                text,
            modifier =
                Modifier.padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            color =
                MaterialTheme
                    .colorScheme
                    .onErrorContainer,
            style =
                MaterialTheme
                    .typography
                    .bodyMedium,
            fontWeight =
                FontWeight.Medium
        )
    }
}


/*
|--------------------------------------------------------------------------
| WARNING DESCRIPTION
|--------------------------------------------------------------------------
*/

private fun buildWarningDescription(
    warningLevel: String
): String {

    val value =
        warningLevel
            .toIntOrNull()


    return if (
        value != null &&
        value in 1..100
    ) {

        "Ứng dụng sẽ cảnh báo khi mức chi tiêu đạt khoảng $value% hạn mức ngân sách."

    } else {

        "Chọn mức phần trăm để ứng dụng cảnh báo trước khi bạn sử dụng hết ngân sách."
    }
}