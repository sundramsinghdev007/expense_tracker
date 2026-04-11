// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/DashboardUiState.kt
package com.sundram.expense_tracker.dashboard

import com.sundram.expense_tracker.domain.model.Budget
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.model.ExpenseFilter

data class DashboardUiState(
    val isLoading: Boolean = true,
    val filteredTotal: Double = 0.0,
    val totalYesterday: Double = 0.0,
    val recentExpenses: List<Expense> = emptyList(),
    val topCategory: Category? = null,
    val errorMessage: String? = null,
    val selectedFilter: ExpenseFilter = ExpenseFilter.Month,
    val budgetAlerts: List<Budget> = emptyList(),
)
