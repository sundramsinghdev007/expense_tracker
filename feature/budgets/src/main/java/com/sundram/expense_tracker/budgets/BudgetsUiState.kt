// feature/budgets/src/main/java/com/sundram/expense_tracker/budgets/BudgetsUiState.kt
package com.sundram.expense_tracker.budgets

import com.sundram.expense_tracker.domain.model.Budget
import java.time.YearMonth

data class BudgetsUiState(
    val isLoading: Boolean = true,
    val budgets: List<Budget> = emptyList(),
    val currentMonth: YearMonth = YearMonth.now(),
    val totalBudgeted: Double = 0.0,
    val totalSpent: Double = 0.0,
    val showAddBudgetSheet: Boolean = false,
)
