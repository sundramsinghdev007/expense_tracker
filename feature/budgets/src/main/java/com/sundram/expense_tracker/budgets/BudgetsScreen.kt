// feature/budgets/src/main/java/com/sundram/expense_tracker/budgets/BudgetsScreen.kt
package com.sundram.expense_tracker.budgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BudgetsScreen(
    viewModel: BudgetsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    BudgetsContent(
        uiState = uiState,
        onAddBudget = viewModel::showAddBudgetSheet,
        onDismissSheet = viewModel::hideAddBudgetSheet,
        onSaveBudget = viewModel::upsertBudget,
    )
}
