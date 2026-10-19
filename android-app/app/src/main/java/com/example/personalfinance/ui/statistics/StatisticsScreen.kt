package com.example.personalfinance.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinance.ui.theme.BrandGold
import com.example.personalfinance.ui.theme.BrandPurple
import com.example.personalfinance.ui.theme.BrandPurpleDark
import com.example.personalfinance.ui.theme.ExpenseRed
import com.example.personalfinance.ui.theme.IncomeGreen
import com.example.personalfinance.viewmodel.StatisticsViewModel
import java.text.NumberFormat
import java.util.Locale


/*
|--------------------------------------------------------------------------
| MODEL RIÊNG CHO BIỂU ĐỒ
|--------------------------------------------------------------------------
*/

private data class MonthChartPoint(

    val month: Int,

    val income: Double,

    val expense: Double
)


@Composable
fun StatisticsScreen(

    onBack: () -> Unit,

    statisticsViewModel: StatisticsViewModel =
        viewModel()

) {

    val state by
    statisticsViewModel
        .uiState
        .collectAsState()


    val formatter =
        NumberFormat.getNumberInstance(
            Locale(
                "vi",
                "VN"
            )
        )


    val monthlyData =
        state.monthlyData


    val categoryData =
        state.categoryData


    /*
    |--------------------------------------------------------------------------
    | DỮ LIỆU BIỂU ĐỒ 12 THÁNG
    |--------------------------------------------------------------------------
    */

    val chartData =
        (1..12).map { monthNumber ->

            val item =
                monthlyData
                    ?.months
                    ?.find {
                        it.month == monthNumber
                    }


            MonthChartPoint(

                month =
                    monthNumber,

                income =
                    item?.total_income ?: 0.0,

                expense =
                    item?.total_expense ?: 0.0
            )
        }


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
                        start = 10.dp,
                        end = 18.dp,
                        top = 12.dp
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
                    Modifier
                        .width(44.dp)
                        .height(44.dp),

                shape =
                    CircleShape,

                color =
                    MaterialTheme
                        .colorScheme
                        .surface
            ) {

                TextButton(
                    onClick =
                        onBack
                ) {

                    Text(

                        text =
                            "‹",

                        style =
                            MaterialTheme
                                .typography
                                .headlineMedium,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.width(10.dp)
            )


            Column(

                modifier =
                    Modifier.weight(1f)

            ) {

                Text(

                    text =
                        "Thống kê tài chính",

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


                Text(

                    text =
                        "Phân tích thu nhập và chi tiêu",

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
        | LOADING LẦN ĐẦU
        |--------------------------------------------------------------------------
        */

        if (
            state.isLoading &&
            monthlyData == null &&
            categoryData == null
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
        | LOADING NHẸ KHI ĐỔI THÁNG / NĂM
        |--------------------------------------------------------------------------
        */

        if (
            state.isLoading &&
            (
                    monthlyData != null ||
                            categoryData != null
                    )
        ) {

            LinearProgressIndicator(

                modifier =
                    Modifier.fillMaxWidth(),

                color =
                    BrandPurple
            )
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
                    top = 18.dp,
                    bottom = 110.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    16.dp
                )

        ) {


            /*
            |--------------------------------------------------------------------------
            | ERROR
            |--------------------------------------------------------------------------
            */

            state.error?.let { error ->

                item {

                    StatisticsErrorCard(

                        message =
                            error,

                        onRetry = {
                            statisticsViewModel
                                .refresh()
                        }
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | CHỌN NĂM
            |--------------------------------------------------------------------------
            */

            item {

                StatisticsPeriodSelector(

                    label =
                        statisticsViewModel
                            .selectedYear
                            .toString(),

                    onPrevious = {
                        statisticsViewModel
                            .previousYear()
                    },

                    onNext = {
                        statisticsViewModel
                            .nextYear()
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | TỔNG QUAN NĂM
            |--------------------------------------------------------------------------
            */

            monthlyData?.let { data ->

                item {

                    StatisticsOverviewCard(

                        year =
                            data.year,

                        totalIncome =
                            data.summary
                                .total_income,

                        totalExpense =
                            data.summary
                                .total_expense,

                        balance =
                            data.summary
                                .balance,

                        savingRate =
                            data.summary
                                .saving_rate,

                        formatter =
                            formatter
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | BIỂU ĐỒ
            |--------------------------------------------------------------------------
            */

            item {

                Column {

                    StatisticsSectionTitle(

                        title =
                            "Biến động thu / chi",

                        subtitle =
                            "So sánh thu nhập và chi tiêu trong 12 tháng"
                    )


                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )


                    MonthlyBarChart(

                        data =
                            chartData,

                        formatter =
                            formatter
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | CHỌN THÁNG
            |--------------------------------------------------------------------------
            */

            item {

                Column {

                    StatisticsSectionTitle(

                        title =
                            "Thống kê theo danh mục",

                        subtitle =
                            "Xem cơ cấu thu hoặc chi theo từng tháng"
                    )


                    Spacer(
                        modifier =
                            Modifier.height(10.dp)
                    )


                    StatisticsPeriodSelector(

                        label =
                            "Tháng ${
                                statisticsViewModel
                                    .selectedMonth
                            }/${
                                statisticsViewModel
                                    .selectedYear
                            }",

                        onPrevious = {
                            statisticsViewModel
                                .previousMonth()
                        },

                        onNext = {
                            statisticsViewModel
                                .nextMonth()
                        }
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | TAB THU / CHI
            |--------------------------------------------------------------------------
            */

            item {

                StatisticsTypeSelector(

                    selectedType =
                        statisticsViewModel
                            .selectedType,

                    onIncomeClick = {

                        statisticsViewModel
                            .changeType(
                                "income"
                            )
                    },

                    onExpenseClick = {

                        statisticsViewModel
                            .changeType(
                                "expense"
                            )
                    }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | CATEGORY TITLE
            |--------------------------------------------------------------------------
            */

            item {

                StatisticsSectionTitle(

                    title =
                        if (
                            statisticsViewModel
                                .selectedType ==
                            "expense"
                        ) {
                            "Chi tiêu theo danh mục"
                        } else {
                            "Thu nhập theo danh mục"
                        },

                    subtitle =
                        if (
                            statisticsViewModel
                                .selectedType ==
                            "expense"
                        ) {
                            "Các danh mục chi tiêu trong tháng"
                        } else {
                            "Các nguồn thu nhập trong tháng"
                        }
                )
            }


            /*
            |--------------------------------------------------------------------------
            | CATEGORY EMPTY
            |--------------------------------------------------------------------------
            */

            if (
                categoryData
                    ?.categories
                    .isNullOrEmpty()
            ) {

                item {

                    StatisticsEmptyCard(

                        text =
                            if (
                                statisticsViewModel
                                    .selectedType ==
                                "expense"
                            ) {
                                "Chưa có dữ liệu chi tiêu trong tháng này."
                            } else {
                                "Chưa có dữ liệu thu nhập trong tháng này."
                            }
                    )
                }

            } else {

                items(

                    items =
                        categoryData!!
                            .categories,

                    key = {
                        it.category_id
                    }

                ) { category ->

                    CategoryStatisticItem(

                        name =
                            category
                                .category_name,

                        amount =
                            formatter.format(
                                category
                                    .total_amount
                            ),

                        percentage =
                            category
                                .percentage,

                        isIncome =
                            statisticsViewModel
                                .selectedType ==
                                    "income"
                    )
                }
            }


            /*
            |--------------------------------------------------------------------------
            | CHI TIẾT 12 THÁNG
            |--------------------------------------------------------------------------
            */

            item {

                StatisticsSectionTitle(

                    title =
                        "Chi tiết theo tháng",

                    subtitle =
                        "Thu nhập, chi tiêu và số dư từng tháng"
                )
            }


            /*
            |--------------------------------------------------------------------------
            | MONTH EMPTY
            |--------------------------------------------------------------------------
            */

            if (
                monthlyData
                    ?.months
                    .isNullOrEmpty()
            ) {

                item {

                    StatisticsEmptyCard(
                        text =
                            "Chưa có dữ liệu theo tháng."
                    )
                }

            } else {

                items(

                    items =
                        monthlyData!!
                            .months,

                    key = {
                        it.month
                    }

                ) { month ->

                    MonthlyStatisticItem(

                        month =
                            month.month,

                        income =
                            month.total_income,

                        expense =
                            month.total_expense,

                        balance =
                            month.balance,

                        formatter =
                            formatter
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| PERIOD SELECTOR
|--------------------------------------------------------------------------
*/

@Composable
private fun StatisticsPeriodSelector(

    label: String,

    onPrevious: () -> Unit,

    onNext: () -> Unit

) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                18.dp
            ),

        color =
            MaterialTheme
                .colorScheme
                .surface,

        tonalElevation =
            2.dp

    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 5.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement.SpaceBetween

        ) {

            TextButton(
                onClick =
                    onPrevious
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
                        BrandPurple
                )
            }


            Text(

                text =
                    label,

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.ExtraBold
            )


            TextButton(
                onClick =
                    onNext
            ) {

                Text(

                    text =
                        "›",

                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        BrandPurple
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| TYPE SELECTOR
|--------------------------------------------------------------------------
*/

@Composable
private fun StatisticsTypeSelector(

    selectedType: String,

    onIncomeClick: () -> Unit,

    onExpenseClick: () -> Unit

) {

    Surface(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                18.dp
            ),

        color =
            MaterialTheme
                .colorScheme
                .surfaceVariant

    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp),

            horizontalArrangement =
                Arrangement.spacedBy(
                    6.dp
                )

        ) {

            Button(

                onClick =
                    onIncomeClick,

                modifier =
                    Modifier.weight(1f),

                shape =
                    RoundedCornerShape(
                        14.dp
                    ),

                colors =
                    ButtonDefaults
                        .buttonColors(

                            containerColor =
                                if (
                                    selectedType ==
                                    "income"
                                ) {
                                    IncomeGreen
                                } else {
                                    Color.Transparent
                                },

                            contentColor =
                                if (
                                    selectedType ==
                                    "income"
                                ) {
                                    Color.White
                                } else {
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                                }
                        )

            ) {

                Text(

                    text =
                        "Thu nhập",

                    fontWeight =
                        FontWeight.Bold
                )
            }


            Button(

                onClick =
                    onExpenseClick,

                modifier =
                    Modifier.weight(1f),

                shape =
                    RoundedCornerShape(
                        14.dp
                    ),

                colors =
                    ButtonDefaults
                        .buttonColors(

                            containerColor =
                                if (
                                    selectedType ==
                                    "expense"
                                ) {
                                    ExpenseRed
                                } else {
                                    Color.Transparent
                                },

                            contentColor =
                                if (
                                    selectedType ==
                                    "expense"
                                ) {
                                    Color.White
                                } else {
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                                }
                        )

            ) {

                Text(

                    text =
                        "Chi tiêu",

                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| SECTION TITLE
|--------------------------------------------------------------------------
*/

@Composable
private fun StatisticsSectionTitle(

    title: String,

    subtitle: String

) {

    Column {

        Text(

            text =
                title,

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
                Modifier.height(3.dp)
        )


        Text(

            text =
                subtitle,

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
| OVERVIEW CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun StatisticsOverviewCard(

    year: Int,

    totalIncome: Double,

    totalExpense: Double,

    balance: Double,

    savingRate: Double,

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
            CardDefaults
                .cardColors(
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

                                listOf(

                                    BrandPurpleDark,

                                    BrandPurple,

                                    Color(
                                        0xFF8158FF
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

                Text(

                    text =
                        "Tổng quan năm $year",

                    color =
                        Color.White.copy(
                            alpha = 0.78f
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(

                    text =
                        "${
                            formatter.format(
                                balance
                            )
                        } đ",

                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,

                    fontWeight =
                        FontWeight.ExtraBold,

                    color =
                        Color.White
                )


                Text(

                    text =
                        "Số dư trong năm",

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,

                    color =
                        Color.White.copy(
                            alpha = 0.70f
                        )
                )


                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )

                ) {

                    OverviewMiniCard(

                        title =
                            "Tổng thu",

                        value =
                            "${
                                formatter.format(
                                    totalIncome
                                )
                            } đ",

                        color =
                            Color(
                                0xFF95F2BD
                            ),

                        modifier =
                            Modifier.weight(1f)
                    )


                    OverviewMiniCard(

                        title =
                            "Tổng chi",

                        value =
                            "${
                                formatter.format(
                                    totalExpense
                                )
                            } đ",

                        color =
                            Color(
                                0xFFFFA4B7
                            ),

                        modifier =
                            Modifier.weight(1f)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(10.dp)
                )


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.spacedBy(
                            10.dp
                        )

                ) {

                    OverviewMiniCard(

                        title =
                            "Số dư",

                        value =
                            "${
                                formatter.format(
                                    balance
                                )
                            } đ",

                        color =
                            if (
                                balance >= 0
                            ) {
                                Color(
                                    0xFF95F2BD
                                )
                            } else {
                                Color(
                                    0xFFFFA4B7
                                )
                            },

                        modifier =
                            Modifier.weight(1f)
                    )


                    OverviewMiniCard(

                        title =
                            "Tỷ lệ tiết kiệm",

                        value =
                            "${
                                formatPercent(
                                    savingRate
                                )
                            }%",

                        color =
                            BrandGold,

                        modifier =
                            Modifier.weight(1f)
                    )
                }
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| OVERVIEW MINI CARD
|--------------------------------------------------------------------------
*/

@Composable
private fun OverviewMiniCard(

    title: String,

    value: String,

    color: Color,

    modifier: Modifier =
        Modifier

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
                    Modifier.height(4.dp)
            )


            Text(

                text =
                    value,

                fontWeight =
                    FontWeight.Bold,

                color =
                    color,

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
| BIỂU ĐỒ 12 THÁNG
|--------------------------------------------------------------------------
*/

@Composable
private fun MonthlyBarChart(

    data: List<MonthChartPoint>,

    formatter: NumberFormat

) {

    val maxValue =
        data
            .flatMap {
                listOf(
                    it.income,
                    it.expense
                )
            }
            .maxOrNull()
            ?.coerceAtLeast(1.0)
            ?: 1.0


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

            /*
            |--------------------------------------------------------------------------
            | LEGEND
            |--------------------------------------------------------------------------
            */

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.End,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                ChartLegend(

                    text =
                        "Thu",

                    color =
                        IncomeGreen
                )


                Spacer(
                    modifier =
                        Modifier.width(14.dp)
                )


                ChartLegend(

                    text =
                        "Chi",

                    color =
                        ExpenseRed
                )
            }


            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )


            /*
            |--------------------------------------------------------------------------
            | BAR
            |--------------------------------------------------------------------------
            */

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            175.dp
                        ),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        3.dp
                    ),

                verticalAlignment =
                    Alignment.Bottom

            ) {

                data.forEach { point ->

                    val incomeHeight =
                        (
                                point.income /
                                        maxValue *
                                        125
                                )
                            .toFloat()
                            .coerceAtLeast(
                                if (
                                    point.income > 0
                                ) {
                                    4f
                                } else {
                                    0f
                                }
                            )


                    val expenseHeight =
                        (
                                point.expense /
                                        maxValue *
                                        125
                                )
                            .toFloat()
                            .coerceAtLeast(
                                if (
                                    point.expense > 0
                                ) {
                                    4f
                                } else {
                                    0f
                                }
                            )


                    Column(

                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxHeight(),

                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.Bottom

                    ) {

                        Row(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(
                                        130.dp
                                    ),

                            horizontalArrangement =
                                Arrangement.spacedBy(
                                    2.dp
                                ),

                            verticalAlignment =
                                Alignment.Bottom

                        ) {

                            Box(

                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(
                                            incomeHeight.dp
                                        )
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp
                                            )
                                        )
                                        .background(
                                            IncomeGreen
                                        )
                            )


                            Box(

                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(
                                            expenseHeight.dp
                                        )
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 4.dp,
                                                topEnd = 4.dp
                                            )
                                        )
                                        .background(
                                            ExpenseRed
                                        )
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(7.dp)
                        )


                        Text(

                            text =
                                point.month
                                    .toString(),

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


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            Text(

                text =
                    "Giá trị cao nhất: ${
                        formatter.format(
                            maxValue
                        )
                    } đ",

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
| CHART LEGEND
|--------------------------------------------------------------------------
*/

@Composable
private fun ChartLegend(

    text: String,

    color: Color

) {

    Row(

        verticalAlignment =
            Alignment.CenterVertically

    ) {

        Box(

            modifier =
                Modifier
                    .width(10.dp)
                    .height(10.dp)
                    .clip(
                        RoundedCornerShape(
                            3.dp
                        )
                    )
                    .background(
                        color
                    )
        )


        Spacer(
            modifier =
                Modifier.width(5.dp)
        )


        Text(

            text =
                text,

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
| CATEGORY
|--------------------------------------------------------------------------
*/

@Composable
private fun CategoryStatisticItem(

    name: String,

    amount: String,

    percentage: Double,

    isIncome: Boolean

) {

    val progressColor =
        if (isIncome) {
            IncomeGreen
        } else {
            ExpenseRed
        }


    val progress =
        (
                percentage.toFloat() /
                        100f
                )
            .coerceIn(
                0f,
                1f
            )


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

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Surface(

                    shape =
                        RoundedCornerShape(
                            14.dp
                        ),

                    color =
                        progressColor.copy(
                            alpha = 0.10f
                        )

                ) {

                    Text(

                        text =
                            if (isIncome) {
                                "↑"
                            } else {
                                "↓"
                            },

                        modifier =
                            Modifier.padding(
                                horizontal = 13.dp,
                                vertical = 9.dp
                            ),

                        color =
                            progressColor,

                        fontWeight =
                            FontWeight.ExtraBold
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )


                Column(

                    modifier =
                        Modifier.weight(1f)

                ) {

                    Text(

                        text =
                            name,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        fontWeight =
                            FontWeight.Bold,

                        maxLines =
                            1,

                        overflow =
                            TextOverflow.Ellipsis
                    )


                    Spacer(
                        modifier =
                            Modifier.height(3.dp)
                    )


                    Text(

                        text =
                            "$amount đ",

                        color =
                            progressColor,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }


                Surface(

                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),

                    color =
                        progressColor.copy(
                            alpha = 0.10f
                        )

                ) {

                    Text(

                        text =
                            "${
                                formatPercent(
                                    percentage
                                )
                            }%",

                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),

                        color =
                            progressColor,

                        fontWeight =
                            FontWeight.Bold,

                        style =
                            MaterialTheme
                                .typography
                                .labelMedium
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(14.dp)
            )


            LinearProgressIndicator(

                progress = {
                    progress
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(
                            RoundedCornerShape(
                                10.dp
                            )
                        ),

                color =
                    progressColor,

                trackColor =
                    MaterialTheme
                        .colorScheme
                        .surfaceVariant
            )
        }
    }
}


/*
|--------------------------------------------------------------------------
| MONTH ITEM
|--------------------------------------------------------------------------
*/

@Composable
private fun MonthlyStatisticItem(

    month: Int,

    income: Double,

    expense: Double,

    balance: Double,

    formatter: NumberFormat

) {

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

                Text(

                    text =
                        "Tháng $month",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )


                Surface(

                    shape =
                        RoundedCornerShape(
                            50.dp
                        ),

                    color =
                        if (
                            balance >= 0
                        ) {
                            IncomeGreen.copy(
                                alpha = 0.10f
                            )
                        } else {
                            ExpenseRed.copy(
                                alpha = 0.10f
                            )
                        }

                ) {

                    Text(

                        text =
                            if (
                                balance >= 0
                            ) {
                                "Thặng dư"
                            } else {
                                "Thâm hụt"
                            },

                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 5.dp
                            ),

                        color =
                            if (
                                balance >= 0
                            ) {
                                IncomeGreen
                            } else {
                                ExpenseRed
                            },

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
                    Modifier.height(14.dp)
            )


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

                Column(

                    modifier =
                        Modifier.padding(
                            14.dp
                        )

                ) {

                    StatisticMoneyRow(

                        title =
                            "Thu nhập",

                        value =
                            "${
                                formatter.format(
                                    income
                                )
                            } đ",

                        valueColor =
                            IncomeGreen
                    )


                    Spacer(
                        modifier =
                            Modifier.height(9.dp)
                    )


                    StatisticMoneyRow(

                        title =
                            "Chi tiêu",

                        value =
                            "${
                                formatter.format(
                                    expense
                                )
                            } đ",

                        valueColor =
                            ExpenseRed
                    )


                    Spacer(
                        modifier =
                            Modifier.height(9.dp)
                    )


                    StatisticMoneyRow(

                        title =
                            "Số dư",

                        value =
                            "${
                                formatter.format(
                                    balance
                                )
                            } đ",

                        valueColor =
                            if (
                                balance >= 0
                            ) {
                                IncomeGreen
                            } else {
                                ExpenseRed
                            }
                    )
                }
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
private fun StatisticMoneyRow(

    title: String,

    value: String,

    valueColor: Color

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
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )


        Text(

            text =
                value,

            color =
                valueColor,

            fontWeight =
                FontWeight.SemiBold
        )
    }
}


/*
|--------------------------------------------------------------------------
| EMPTY
|--------------------------------------------------------------------------
*/

@Composable
private fun StatisticsEmptyCard(

    text: String

) {

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
            )

    ) {

        Box(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        22.dp
                    ),

            contentAlignment =
                Alignment.Center

        ) {

            Text(

                text =
                    text,

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

@Composable
private fun StatisticsErrorCard(

    message: String,

    onRetry: () -> Unit

) {

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
                        .errorContainer
            )

    ) {

        Column(

            modifier =
                Modifier.padding(
                    16.dp
                )

        ) {

            Text(

                text =
                    message,

                color =
                    MaterialTheme
                        .colorScheme
                        .onErrorContainer
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            TextButton(
                onClick =
                    onRetry
            ) {

                Text(
                    text =
                        "Thử lại"
                )
            }
        }
    }
}


/*
|--------------------------------------------------------------------------
| FORMAT %
|--------------------------------------------------------------------------
*/

private fun formatPercent(

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
            Locale(
                "vi",
                "VN"
            ),
            "%.1f",
            value
        )
    }
}