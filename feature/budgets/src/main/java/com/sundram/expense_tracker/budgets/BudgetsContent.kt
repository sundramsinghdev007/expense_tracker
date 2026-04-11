// feature/budgets/src/main/java/com/sundram/expense_tracker/budgets/BudgetsContent.kt
package com.sundram.expense_tracker.budgets

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sundram.expense_tracker.budgets.component.AddBudgetSheet
import com.sundram.expense_tracker.budgets.component.BudgetCard
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.ui.component.EmptyState
import com.sundram.expense_tracker.ui.component.ExpenseTrackerTopBar
import com.sundram.expense_tracker.ui.component.LoadingIndicator

@Composable
fun BudgetsContent(
    uiState: BudgetsUiState,
    onAddBudget: () -> Unit,
    onDismissSheet: () -> Unit,
    onSaveBudget: (Category, Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ExpenseTrackerTopBar(title = stringResource(R.string.budgets_title))
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddBudget) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.budgets_add_cd),
                )
            }
        },
    ) { innerPadding ->
        BudgetsBody(
            uiState = uiState,
            modifier = Modifier.padding(innerPadding),
        )
    }

    if (uiState.showAddBudgetSheet) {
        AddBudgetSheet(
            onDismiss = onDismissSheet,
            onSave = onSaveBudget,
        )
    }
}

@Composable
private fun BudgetsBody(
    uiState: BudgetsUiState,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> LoadingIndicator(modifier = modifier)
        uiState.budgets.isEmpty() -> EmptyState(
            message = stringResource(R.string.budgets_empty),
            emoji = "💰",
            modifier = modifier,
        )
        else -> LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(vertical = com.sundram.expense_tracker.ui.theme.Dimens.spacingSm),
        ) {
            items(uiState.budgets, key = { it.id }) { budget ->
                BudgetCard(
                    budget = budget,
                    onClick = {},
                )
            }
        }
    }
}
