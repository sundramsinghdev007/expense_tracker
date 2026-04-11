// feature/analytics/src/main/java/com/sundram/expense_tracker/analytics/AnalyticsContent.kt
package com.sundram.expense_tracker.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sundram.expense_tracker.analytics.component.CategoryBreakdownChart
import com.sundram.expense_tracker.analytics.component.CategoryHeader
import com.sundram.expense_tracker.analytics.component.DailySpendChart
import com.sundram.expense_tracker.analytics.component.PeriodSelector
import com.sundram.expense_tracker.ui.component.ExpenseTrackerTopBar
import com.sundram.expense_tracker.ui.component.LoadingIndicator
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun AnalyticsContent(
    uiState: AnalyticsUiState,
    onPeriodChange: (Period) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ExpenseTrackerTopBar(
                title = stringResource(R.string.analytics_title),
            )
        },
    ) { innerPadding ->
        if (uiState.isLoading) {
            LoadingIndicator(modifier = Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(Dimens.spacingMd),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingMd),
            ) {
                PeriodSelector(
                    selectedPeriod = uiState.selectedPeriod,
                    onPeriodChange = onPeriodChange,
                )
                CategoryHeader(
                    totalSpend = uiState.totalSpend,
                    trend = uiState.trend,
                )
                CategoryBreakdownChart(
                    categoryBreakdown = uiState.categoryBreakdown,
                )
                DailySpendChart(
                    dailySpend = uiState.dailySpend,
                )
            }
        }
    }
}
