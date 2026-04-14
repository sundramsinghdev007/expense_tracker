// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/DashboardContent.kt
package com.sundram.expense_tracker.dashboard

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sundram.expense_tracker.dashboard.component.BudgetAlertCard
import com.sundram.expense_tracker.dashboard.component.DashboardSearchBar
import com.sundram.expense_tracker.dashboard.component.ExpenseCard
import com.sundram.expense_tracker.dashboard.component.FilterChipRow
import com.sundram.expense_tracker.dashboard.component.SummaryHeader
import com.sundram.expense_tracker.domain.model.ExpenseFilter
import com.sundram.expense_tracker.ui.component.EmptyState
import com.sundram.expense_tracker.ui.component.ErrorState
import com.sundram.expense_tracker.ui.component.ExpenseTrackerTopBar
import com.sundram.expense_tracker.ui.component.LoadingIndicator
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onAddExpense: () -> Unit,
    onScanReceipt: () -> Unit,
    onExpenseClick: (Long) -> Unit,
    onFilterSelected: (ExpenseFilter) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    onSearchHistoryItemClick: (String) -> Unit,
    onSearchHistoryItemRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ExpenseTrackerTopBar(
                title = stringResource(R.string.dashboard_title),
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExpense) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.dashboard_add_expense_cd),
                )
            }
        },
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                LoadingIndicator(modifier = Modifier.padding(innerPadding))
            }
            uiState.errorMessage != null -> {
                ErrorState(
                    message = uiState.errorMessage,
                    retryLabel = stringResource(R.string.dashboard_retry_label),
                    onRetry = { /* retry handled by ViewModel resubscription */ },
                    modifier = Modifier.padding(innerPadding),
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                ) {
                    item {
                        DashboardSearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = onSearchQueryChange,
                            onSearchSubmit = onSearchSubmit,
                            searchHistory = uiState.searchHistory,
                            onHistoryItemClick = onSearchHistoryItemClick,
                            onHistoryItemRemove = onSearchHistoryItemRemove,
                        )
                    }
                    item {
                        FilterChipRow(
                            selectedFilter = uiState.selectedFilter,
                            onFilterSelected = onFilterSelected,
                        )
                    }
                    if (uiState.budgetAlerts.isNotEmpty()) {
                        item {
                            Text(
                                text = stringResource(R.string.dashboard_budget_alerts_title),
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(
                                    horizontal = Dimens.spacingMd,
                                    vertical = Dimens.spacingXs,
                                ),
                            )
                        }
                        items(uiState.budgetAlerts, key = { "alert_${it.id}" }) { budget ->
                            BudgetAlertCard(budget = budget)
                        }
                    }
                    item {
                        SummaryHeader(
                            filteredTotal = uiState.filteredTotal,
                            topCategory = uiState.topCategory,
                            selectedFilter = uiState.selectedFilter,
                        )
                    }
                    if (uiState.recentExpenses.isEmpty()) {
                        item {
                            val emptyMessage = if (uiState.searchQuery.isNotEmpty()) {
                                stringResource(R.string.dashboard_search_empty_message)
                            } else {
                                stringResource(R.string.dashboard_empty_message)
                            }
                            val emptyEmoji = if (uiState.searchQuery.isNotEmpty()) "🔍" else "💸"
                            EmptyState(
                                message = emptyMessage,
                                emoji = emptyEmoji,
                                modifier = Modifier.fillParentMaxSize(),
                            )
                        }
                    } else {
                        items(uiState.recentExpenses, key = { it.id }) { expense ->
                            ExpenseCard(
                                expense = expense,
                                onClick = { onExpenseClick(expense.id) },
                            )
                        }
                    }
                }
            }
        }
    }
}
