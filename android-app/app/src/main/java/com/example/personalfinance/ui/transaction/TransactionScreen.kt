package com.example.personalfinance.ui.transaction

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable

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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.personalfinance.data.model.Transaction
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.viewmodel.TransactionViewModel

import java.text.NumberFormat
import java.time.YearMonth
import java.util.Locale


/*
|--------------------------------------------------------------------------
| FILTER
|--------------------------------------------------------------------------
*/

private enum class TransactionFilter {
    ALL,
    INCOME,
    EXPENSE
}


/*
|--------------------------------------------------------------------------
| SORT
|--------------------------------------------------------------------------
*/

private enum class TransactionSort {
    NEWEST,
    OLDEST,
    AMOUNT_HIGH
}


@Composable
fun TransactionScreen(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onEditClick: (Long) -> Unit,
    transactionViewModel: TransactionViewModel = viewModel()
) {

    val state by
    transactionViewModel
        .uiState
        .collectAsState()


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
    | LOAD
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(Unit) {

        transactionViewModel
            .loadTransactions()
    }


    /*
    |--------------------------------------------------------------------------
    | UI STATE
    |--------------------------------------------------------------------------
    */

    var selectedFilter by remember {
        mutableStateOf(
            TransactionFilter.ALL
        )
    }


    var selectedSort by remember {
        mutableStateOf(
            TransactionSort.NEWEST
        )
    }


    var selectedMonth by remember {
        mutableStateOf(
            YearMonth.now()
        )
    }


    var searchVisible by remember {
        mutableStateOf(false)
    }


    var searchText by remember {
        mutableStateOf("")
    }


    var monthExpanded by remember {
        mutableStateOf(false)
    }


    var sortExpanded by remember {
        mutableStateOf(false)
    }


    var transactionIdToDelete by remember {
        mutableStateOf<Long?>(null)
    }


    /*
    |--------------------------------------------------------------------------
    | MONTH
    |--------------------------------------------------------------------------
    */

    val monthPrefix =
        String.format(
            Locale.US,
            "%04d-%02d",
            selectedMonth.year,
            selectedMonth.monthValue
        )


    val transactionsInMonth =
        state.transactions.filter {

            it.transaction_date
                .startsWith(
                    monthPrefix
                )
        }


    /*
    |--------------------------------------------------------------------------
    | TYPE
    |--------------------------------------------------------------------------
    */

    val filteredByType =
        transactionsInMonth.filter {

            when (
                selectedFilter
            ) {

                TransactionFilter.ALL ->
                    true

                TransactionFilter.INCOME ->
                    it.type == "income"

                TransactionFilter.EXPENSE ->
                    it.type == "expense"
            }
        }


    /*
    |--------------------------------------------------------------------------
    | SEARCH
    |--------------------------------------------------------------------------
    */

    val filteredBySearch =
        filteredByType.filter {
                transaction ->

            if (
                searchText.isBlank()
            ) {

                true

            } else {

                val keyword =
                    searchText
                        .trim()
                        .lowercase()


                transaction
                    .category_name
                    .lowercase()
                    .contains(
                        keyword
                    ) ||

                        transaction
                            .merchant_name
                            ?.lowercase()
                            ?.contains(
                                keyword
                            ) == true ||

                        transaction
                            .note
                            ?.lowercase()
                            ?.contains(
                                keyword
                            ) == true
            }
        }


    /*
    |--------------------------------------------------------------------------
    | SORT
    |--------------------------------------------------------------------------
    */

    val transactions =
        when (
            selectedSort
        ) {

            TransactionSort.NEWEST -> {

                filteredBySearch
                    .sortedWith(
                        compareByDescending<Transaction> {
                            it.transaction_date
                        }
                            .thenByDescending {
                                it.transaction_time
                                    ?: ""
                            }
                            .thenByDescending {
                                it.id
                            }
                    )
            }


            TransactionSort.OLDEST -> {

                filteredBySearch
                    .sortedWith(
                        compareBy<Transaction> {
                            it.transaction_date
                        }
                            .thenBy {
                                it.transaction_time
                                    ?: ""
                            }
                    )
            }


            TransactionSort.AMOUNT_HIGH -> {

                filteredBySearch
                    .sortedByDescending {
                        it.amount
                    }
            }
        }


    /*
    |--------------------------------------------------------------------------
    | TOTALS
    |--------------------------------------------------------------------------
    */

    val totalIncome =
        transactionsInMonth
            .filter {
                it.type == "income"
            }
            .sumOf {
                it.amount
            }


    val totalExpense =
        transactionsInMonth
            .filter {
                it.type == "expense"
            }
            .sumOf {
                it.amount
            }


    val balance =
        totalIncome -
                totalExpense


    /*
    |--------------------------------------------------------------------------
    | DELETE DIALOG
    |--------------------------------------------------------------------------
    */

    if (
        transactionIdToDelete != null
    ) {

        AlertDialog(
            onDismissRequest = {

                transactionIdToDelete =
                    null
            },
            shape =
                RoundedCornerShape(
                    24.dp
                ),
            title = {

                Text(
                    text =
                        "Xóa giao dịch?",
                    fontWeight =
                        FontWeight.ExtraBold
                )
            },
            text = {

                Text(
                    text =
                        "Bạn có chắc chắn muốn xóa giao dịch này không?"
                )
            },
            confirmButton = {

                Button(
                    onClick = {

                        val id =
                            transactionIdToDelete


                        if (
                            id != null
                        ) {

                            transactionViewModel
                                .deleteTransaction(
                                    id =
                                        id,
                                    onSuccess = {

                                        transactionIdToDelete =
                                            null
                                    }
                                )
                        }
                    },
                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),
                    colors =
                        ButtonDefaults
                            .buttonColors(
                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .error
                            )
                ) {

                    Text(
                        text =
                            "Xóa giao dịch",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            },
            dismissButton = {

                TextButton(
                    onClick = {

                        transactionIdToDelete =
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

        TransactionHeader(
            onBack =
                onBack,
            onAddClick =
                onAddClick,
            onSearchClick = {

                searchVisible =
                    !searchVisible

                if (
                    !searchVisible
                ) {

                    searchText =
                        ""
                }
            },
            onSortClick = {

                sortExpanded =
                    true
            },
            sortExpanded =
                sortExpanded,
            onDismissSort = {

                sortExpanded =
                    false
            },
            onSortSelected = {

                selectedSort =
                    it

                sortExpanded =
                    false
            }
        )


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
                    bottom = 110.dp
                ),
            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )
        ) {


            /*
            |--------------------------------------------------------------------------
            | SEARCH
            |--------------------------------------------------------------------------
            */

            if (
                searchVisible
            ) {

                item {

                    OutlinedTextField(
                        value =
                            searchText,
                        onValueChange = {

                            searchText =
                                it
                        },
                        modifier =
                            Modifier.fillMaxWidth(),
                        placeholder = {
                        },
                        leadingIcon = {

                            Icon(
                                imageVector =
                                    Icons.Default.Search,
                                contentDescription =
                                    null
                            )
                        },
                        singleLine =
                            true,
                        shape =
                            RoundedCornerShape(
                                16.dp
                            )
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | SEGMENT
            |--------------------------------------------------------------------------
            */

            item {

                TransactionSegment(
                    selected =
                        selectedFilter,
                    onSelected = {

                        selectedFilter =
                            it
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | MONTH
            |--------------------------------------------------------------------------
            */

            item {

                Row(
                    modifier =
                        Modifier.fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Box {

                        Surface(
                            modifier =
                                Modifier.clickable {

                                    monthExpanded =
                                        true
                                },
                            shape =
                                RoundedCornerShape(
                                    14.dp
                                ),
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .surface,
                            tonalElevation =
                                1.dp
                        ) {

                            Row(
                                modifier =
                                    Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 9.dp
                                    ),
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
                                            18.dp
                                        ),
                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                )


                                Spacer(
                                    modifier =
                                        Modifier.width(
                                            7.dp
                                        )
                                )


                                Text(
                                    text =
                                        "Tháng ${selectedMonth.monthValue}/${selectedMonth.year}",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodyMedium,
                                    fontWeight =
                                        FontWeight.Bold
                                )


                                Spacer(
                                    modifier =
                                        Modifier.width(
                                            5.dp
                                        )
                                )


                                Text(
                                    text =
                                        "⌄",
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .primary,
                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }


                        DropdownMenu(
                            expanded =
                                monthExpanded,
                            onDismissRequest = {

                                monthExpanded =
                                    false
                            }
                        ) {

                            val current =
                                YearMonth.now()


                            repeat(
                                12
                            ) {
                                    index ->

                                val month =
                                    current.minusMonths(
                                        index.toLong()
                                    )


                                DropdownMenuItem(
                                    text = {

                                        Text(
                                            "Tháng ${month.monthValue}/${month.year}"
                                        )
                                    },
                                    onClick = {

                                        selectedMonth =
                                            month

                                        monthExpanded =
                                            false
                                    }
                                )
                            }
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    )


                    Text(
                        text =
                            "${transactions.size} giao dịch",
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
            | LOADING
            |--------------------------------------------------------------------------
            */

            if (
                state.isLoading
            ) {

                item {

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(
                                    220.dp
                                ),
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

                return@LazyColumn
            }


            /*
            |--------------------------------------------------------------------------
            | ERROR
            |--------------------------------------------------------------------------
            */

            if (
                state.error != null
            ) {

                item {

                    TransactionErrorCard(
                        message =
                            state.error
                                ?: "Có lỗi xảy ra.",
                        onRetry = {

                            transactionViewModel
                                .loadTransactions()
                        }
                    )
                }

                return@LazyColumn
            }


            /*
            |--------------------------------------------------------------------------
            | SUMMARY
            |--------------------------------------------------------------------------
            */

            item {

                TransactionSummaryCard(
                    totalIncome =
                        totalIncome,
                    totalExpense =
                        totalExpense,
                    balance =
                        balance,
                    formatter =
                        formatter
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
                            "Danh sách giao dịch",
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
                            when (
                                selectedSort
                            ) {

                                TransactionSort.NEWEST ->
                                    "Mới nhất"

                                TransactionSort.OLDEST ->
                                    "Cũ nhất"

                                TransactionSort.AMOUNT_HIGH ->
                                    "Số tiền cao"
                            },
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


            /*
            |--------------------------------------------------------------------------
            | EMPTY
            |--------------------------------------------------------------------------
            */

            if (transactions.isEmpty()) {
                item {
                    TransactionEmptyState(
                        hasTransactionsInMonth = transactionsInMonth.isNotEmpty(),
                        hasTransactionsAfterTypeFilter = filteredByType.isNotEmpty(),
                        hasSearchText = searchText.isNotBlank(),
                        onAddClick = onAddClick
                    )
                }


            } else {


                /*
                |--------------------------------------------------------------------------
                | LIST CARD
                |--------------------------------------------------------------------------
                */

                item {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                22.dp
                            ),
                        colors =
                            CardDefaults
                                .cardColors(
                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .surface
                                ),
                        elevation =
                            CardDefaults
                                .cardElevation(
                                    defaultElevation =
                                        2.dp
                                )
                    ) {

                        Column(
                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            transactions
                                .forEachIndexed {
                                        index,
                                        transaction ->

                                    CompactTransactionRow(
                                        transaction =
                                            transaction,
                                        formatter =
                                            formatter,
                                        onClick = {

                                            onEditClick(
                                                transaction.id
                                            )
                                        },
                                        onLongClick = {

                                            transactionIdToDelete =
                                                transaction.id
                                        }
                                    )


                                    if (
                                        index <
                                        transactions.lastIndex
                                    ) {

                                        HorizontalDivider(
                                            modifier =
                                                Modifier.padding(
                                                    start = 66.dp,
                                                    end = 14.dp
                                                ),
                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .outlineVariant
                                                    .copy(
                                                        alpha = 0.45f
                                                    )
                                        )
                                    }
                                }
                        }
                    }
                }


                /*
                |--------------------------------------------------------------------------
                | DELETE TIP
                |--------------------------------------------------------------------------
                */

                item {

                    Text(
                        text =
                            "Nhấn giữ để xóa",
                        modifier =
                            Modifier.fillMaxWidth(),
                        style =
                            MaterialTheme
                                .typography
                                .labelSmall,
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
    }
}


/*
|--------------------------------------------------------------------------
| HEADER
|--------------------------------------------------------------------------
*/

@Composable
private fun TransactionHeader(
    onBack: () -> Unit,
    onAddClick: () -> Unit,
    onSearchClick: () -> Unit,
    onSortClick: () -> Unit,
    sortExpanded: Boolean,
    onDismissSort: () -> Unit,
    onSortSelected: (TransactionSort) -> Unit
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
                        start = 13.dp
                    )
        ) {

            Text(
                text =
                    "Giao dịch",
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
        }


        /*
        |--------------------------------------------------------------------------
        | SEARCH
        |--------------------------------------------------------------------------
        */

        IconButton(
            onClick =
                onSearchClick
        ) {

            Icon(
                imageVector =
                    Icons.Default.Search,
                contentDescription =
                    "Tìm kiếm"
            )
        }


        /*
        |--------------------------------------------------------------------------
        | SORT
        |--------------------------------------------------------------------------
        */

        Box {

            IconButton(
                onClick =
                    onSortClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.FilterList,
                    contentDescription =
                        "Sắp xếp"
                )
            }


            DropdownMenu(
                expanded =
                    sortExpanded,
                onDismissRequest =
                    onDismissSort
            ) {

                DropdownMenuItem(
                    text = {

                        Text(
                            "Mới nhất"
                        )
                    },
                    onClick = {

                        onSortSelected(
                            TransactionSort.NEWEST
                        )
                    }
                )


                DropdownMenuItem(
                    text = {

                        Text(
                            "Cũ nhất"
                        )
                    },
                    onClick = {

                        onSortSelected(
                            TransactionSort.OLDEST
                        )
                    }
                )


                DropdownMenuItem(
                    text = {

                        Text(
                            "Số tiền cao nhất"
                        )
                    },
                    onClick = {

                        onSortSelected(
                            TransactionSort.AMOUNT_HIGH
                        )
                    }
                )
            }
        }


        /*
        |--------------------------------------------------------------------------
        | ADD
        |--------------------------------------------------------------------------
        */

        Surface(
            modifier =
                Modifier.size(
                    42.dp
                ),
            shape =
                RoundedCornerShape(
                    14.dp
                ),
            color =
                MaterialTheme
                    .colorScheme
                    .primary
        ) {

            IconButton(
                onClick =
                    onAddClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,
                    contentDescription =
                        "Thêm giao dịch",
                    tint =
                        MaterialTheme
                            .colorScheme
                            .onPrimary
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| SEGMENT
|--------------------------------------------------------------------------
*/

@Composable
private fun TransactionSegment(
    selected: TransactionFilter,
    onSelected: (TransactionFilter) -> Unit
) {

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
                    alpha = 0.55f
                )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    4.dp
                )
        ) {

            TransactionSegmentItem(
                text =
                    "Tất cả",
                selected =
                    selected ==
                            TransactionFilter.ALL,
                modifier =
                    Modifier.weight(
                        1f
                    ),
                onClick = {

                    onSelected(
                        TransactionFilter.ALL
                    )
                }
            )


            TransactionSegmentItem(
                text =
                    "Thu nhập",
                selected =
                    selected ==
                            TransactionFilter.INCOME,
                modifier =
                    Modifier.weight(
                        1f
                    ),
                onClick = {

                    onSelected(
                        TransactionFilter.INCOME
                    )
                }
            )


            TransactionSegmentItem(
                text =
                    "Chi tiêu",
                selected =
                    selected ==
                            TransactionFilter.EXPENSE,
                modifier =
                    Modifier.weight(
                        1f
                    ),
                onClick = {

                    onSelected(
                        TransactionFilter.EXPENSE
                    )
                }
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| SEGMENT ITEM
|--------------------------------------------------------------------------
*/

@Composable
private fun TransactionSegmentItem(
    text: String,
    selected: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
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
            if (
                selected
            ) {

                MaterialTheme
                    .colorScheme
                    .primary

            } else {

                Color.Transparent
            }
    ) {

        Box(
            modifier =
                Modifier.padding(
                    vertical = 9.dp
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    text,
                style =
                    MaterialTheme
                        .typography
                        .labelMedium,
                color =
                    if (
                        selected
                    ) {

                        MaterialTheme
                            .colorScheme
                            .onPrimary

                    } else {

                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                    },
                fontWeight =
                    if (
                        selected
                    ) {

                        FontWeight.Bold

                    } else {

                        FontWeight.Medium
                    }
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
private fun TransactionSummaryCard(
    totalIncome: Double,
    totalExpense: Double,
    balance: Double,
    formatter: NumberFormat
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
                modifier =
                    Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text =
                            "Chênh lệch thu chi",
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
                                if (
                                    balance > 0
                                ) {
                                    "+"
                                } else {
                                    ""
                                }
                            }${
                                formatter.format(
                                    balance
                                )
                            } đ",
                        style =
                            MaterialTheme
                                .typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.ExtraBold,
                        color =
                            when {

                                balance > 0 ->
                                    IncomeGreen

                                balance < 0 ->
                                    ExpenseRed

                                else ->
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface
                            }
                    )
                }


                Surface(
                    modifier =
                        Modifier.size(
                            48.dp
                        ),
                    shape =
                        RoundedCornerShape(
                            15.dp
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
                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary,
                            modifier =
                                Modifier.size(
                                    25.dp
                                )
                        )
                    }
                }
            }


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

                TransactionSummaryItem(
                    title =
                        "Tổng thu",
                    value =
                        "${
                            formatter.format(
                                totalIncome
                            )
                        } đ",
                    symbol =
                        "↓",
                    color =
                        IncomeGreen,
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                TransactionSummaryItem(
                    title =
                        "Tổng chi",
                    value =
                        "${
                            formatter.format(
                                totalExpense
                            )
                        } đ",
                    symbol =
                        "↑",
                    color =
                        ExpenseRed,
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
private fun TransactionSummaryItem(
    title: String,
    value: String,
    symbol: String,
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
            color.copy(
                alpha = 0.08f
            )
    ) {

        Row(
            modifier =
                Modifier.padding(
                    12.dp
                ),
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
                    color.copy(
                        alpha = 0.12f
                    )
            ) {

                Box(
                    contentAlignment =
                        Alignment.Center
                ) {

                    Text(
                        text =
                            symbol,
                        color =
                            color,
                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(
                        8.dp
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
                        title,
                    style =
                        MaterialTheme
                            .typography
                            .labelSmall,
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
                    color =
                        color,
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
}


/*
|--------------------------------------------------------------------------
| TRANSACTION ROW
|--------------------------------------------------------------------------
*/

@OptIn(
    ExperimentalFoundationApi::class
)
@Composable
private fun CompactTransactionRow(
    transaction: Transaction,
    formatter: NumberFormat,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {

    val isIncome =
        transaction.type ==
                "income"


    val isQr =
        transaction.payment_method ==
                "qr_payment"


    val amountColor =
        if (
            isIncome
        ) {

            IncomeGreen

        } else {

            ExpenseRed
        }


    val title =
        if (
            isQr &&
            !transaction
                .merchant_name
                .isNullOrBlank()
        ) {

            transaction
                .merchant_name
                .orEmpty()

        } else {

            transaction
                .category_name
        }


    val subtitle =
        when {

            isQr ->
                "${transaction.category_name} • Thanh toán QR"

            isIncome ->
                "Thu nhập • ${transaction.category_name}"

            else ->
                "Chi tiêu • ${transaction.category_name}"
        }


    val dateTimeText =
        buildTransactionDateTime(
            date =
                transaction.transaction_date,
            time =
                transaction.transaction_time
        )


    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick =
                        onClick,
                    onLongClick =
                        onLongClick
                )
                .padding(
                    horizontal = 13.dp,
                    vertical = 11.dp
                ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {


        /*
        |--------------------------------------------------------------------------
        | ICON
        |--------------------------------------------------------------------------
        */

        TransactionIcon(
            transaction =
                transaction,
            color =
                amountColor,
            isQr =
                isQr,
            isIncome =
                isIncome
        )


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
                Modifier.weight(
                    1f
                )
        ) {

            Text(
                text =
                    title,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                fontWeight =
                    FontWeight.Bold,
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
                        2.dp
                    )
            )


            Text(
                text =
                    subtitle,
                style =
                    MaterialTheme
                        .typography
                        .labelSmall,
                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,
                maxLines =
                    1,
                overflow =
                    TextOverflow.Ellipsis
            )


            if (
                !transaction.note
                    .isNullOrBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(
                            2.dp
                        )
                )


                Text(
                    text =
                        transaction.note
                            ?: "",
                    style =
                        MaterialTheme
                            .typography
                            .labelSmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,
                    maxLines =
                        1,
                    overflow =
                        TextOverflow.Ellipsis
                )
            }
        }


        Spacer(
            modifier =
                Modifier.width(
                    8.dp
                )
        )


        /*
        |--------------------------------------------------------------------------
        | AMOUNT
        |--------------------------------------------------------------------------
        */

        Column(
            horizontalAlignment =
                Alignment.End
        ) {

            Text(
                text =
                    if (
                        isIncome
                    ) {

                        "+${
                            formatter.format(
                                transaction.amount
                            )
                        } đ"

                    } else {

                        "-${
                            formatter.format(
                                transaction.amount
                            )
                        } đ"
                    },
                color =
                    amountColor,
                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,
                fontWeight =
                    FontWeight.ExtraBold,
                maxLines =
                    1
            )


            Spacer(
                modifier =
                    Modifier.height(
                        3.dp
                    )
            )


            Text(
                text =
                    dateTimeText,
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
}


/*
|--------------------------------------------------------------------------
| ICON
|--------------------------------------------------------------------------
*/

@Composable
private fun TransactionIcon(
    transaction: Transaction,
    color: Color,
    isQr: Boolean,
    isIncome: Boolean
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
            color.copy(
                alpha = 0.10f
            )
    ) {

        Box(
            contentAlignment =
                Alignment.Center
        ) {

            if (
                isQr
            ) {

                Text(
                    text =
                        "QR",
                    color =
                        ExpenseRed,
                    style =
                        MaterialTheme
                            .typography
                            .labelMedium,
                    fontWeight =
                        FontWeight.ExtraBold
                )

            } else {

                Icon(
                    imageVector =
                        getTransactionCategoryIcon(
                            category =
                                transaction.category_name,
                            isIncome =
                                isIncome
                        ),
                    contentDescription =
                        transaction.category_name,
                    tint =
                        color,
                    modifier =
                        Modifier.size(
                            21.dp
                        )
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| EMPTY
|--------------------------------------------------------------------------
*/
@Composable
private fun TransactionEmptyState(
    hasTransactionsInMonth: Boolean,
    hasTransactionsAfterTypeFilter: Boolean,
    hasSearchText: Boolean,
    onAddClick: () -> Unit
) {
    val message = when {
        // Tháng này hoàn toàn chưa có giao dịch
        !hasTransactionsInMonth ->
            "Chưa có giao dịch"

        // Có giao dịch nhưng không có giao dịch thuộc loại
        // Thu nhập / Chi tiêu đang chọn
        !hasTransactionsAfterTypeFilter ->
            "Chưa có giao dịch"

        // Có giao dịch đúng loại nhưng tìm kiếm không ra
        hasSearchText ->
            "Không tìm thấy giao dịch"

        else ->
            "Chưa có giao dịch"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                modifier = Modifier.size(76.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Chỉ hiện nút thêm khi chưa có giao dịch
            // thuộc loại đang chọn hoặc tháng hoàn toàn chưa có giao dịch
            if (!hasTransactionsAfterTypeFilter) {
                Spacer(modifier = Modifier.height(17.dp))

                Button(
                    onClick = onAddClick,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Thêm giao dịch",
                        fontWeight = FontWeight.Bold
                    )
                }
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
private fun TransactionErrorCard(
    message: String,
    onRetry: () -> Unit
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(
                22.dp
            ),
        colors =
            CardDefaults
                .cardColors(
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
                    "Không thể tải giao dịch",
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


/*
|--------------------------------------------------------------------------
| CATEGORY ICON
|--------------------------------------------------------------------------
*/

private fun getTransactionCategoryIcon(
    category: String,
    isIncome: Boolean
): ImageVector {

    if (
        isIncome
    ) {

        return Icons.Default
            .AccountBalanceWallet
    }


    val value =
        category.lowercase()


    return when {

        value.contains(
            "ăn"
        ) ->
            Icons.Default.Restaurant


        value.contains(
            "đi"
        ) ->
            Icons.Default.DirectionsCar


        value.contains(
            "mua"
        ) ->
            Icons.Default.ShoppingCart


        value.contains(
            "nhà"
        ) ->
            Icons.Default.Home


        value.contains(
            "học"
        ) ->
            Icons.Default.School


        value.contains(
            "y tế"
        ) ->
            Icons.Default.LocalHospital


        value.contains(
            "giải trí"
        ) ->
            Icons.Default.Movie


        value.contains(
            "hóa đơn"
        ) ->
            Icons.Default.ReceiptLong


        else ->
            Icons.Default.AccountBalanceWallet
    }
}


/*
|--------------------------------------------------------------------------
| DATE + TIME
|--------------------------------------------------------------------------
*/

private fun buildTransactionDateTime(
    date: String,
    time: String?
): String {

    val formattedDate =
        formatTransactionDate(
            date
        )


    val formattedTime =
        formatTransactionTime(
            time
        )


    return if (
        formattedTime.isBlank()
    ) {

        formattedDate

    } else {

        "$formattedDate • $formattedTime"
    }
}


/*
|--------------------------------------------------------------------------
| DATE
|--------------------------------------------------------------------------
*/

private fun formatTransactionDate(
    date: String
): String {

    return try {

        val parts =
            date.split(
                "-"
            )


        if (
            parts.size ==
            3
        ) {

            "${parts[2]}/${parts[1]}"

        } else {

            date
        }

    } catch (_: Exception) {

        date
    }
}


/*
|--------------------------------------------------------------------------
| TIME
|--------------------------------------------------------------------------
*/

private fun formatTransactionTime(
    time: String?
): String {

    if (
        time.isNullOrBlank()
    ) {

        return ""
    }


    return try {

        if (
            time.length >=
            5
        ) {

            time.substring(
                0,
                5
            )

        } else {

            time
        }

    } catch (_: Exception) {

        time
    }
}