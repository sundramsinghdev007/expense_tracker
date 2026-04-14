// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/DashboardScreen.kt
package com.sundram.expense_tracker.dashboard

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun DashboardScreen(
    onAddExpense: () -> Unit,
    onScanReceipt: () -> Unit,
    onExpenseClick: (Long) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardContent(
        uiState = uiState,
        onAddExpense = onAddExpense,
        onScanReceipt = onScanReceipt,
        onExpenseClick = onExpenseClick,
        onFilterSelected = viewModel::onFilterSelected,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSearchSubmit = viewModel::onSearchSubmit,
        onSearchHistoryItemClick = viewModel::onSearchHistoryItemClick,
        onSearchHistoryItemRemove = viewModel::onSearchHistoryItemRemove,
    )
}
