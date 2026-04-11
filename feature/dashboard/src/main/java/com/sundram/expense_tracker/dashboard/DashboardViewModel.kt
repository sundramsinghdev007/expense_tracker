// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/DashboardViewModel.kt
package com.sundram.expense_tracker.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundram.expense_tracker.domain.model.ExpenseFilter
import com.sundram.expense_tracker.domain.usecase.GetBudgetsUseCase
import com.sundram.expense_tracker.domain.usecase.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getBudgetsUseCase: GetBudgetsUseCase,
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<ExpenseFilter>(ExpenseFilter.Month)

    val uiState: StateFlow<DashboardUiState> = combine(
        getExpensesUseCase(),
        getBudgetsUseCase(YearMonth.now()),
        _selectedFilter,
    ) { expenses, budgets, filter ->
        val now = LocalDate.now()

        val filteredExpenses = expenses.filter { expense ->
            when (filter) {
                ExpenseFilter.Day -> expense.date == now
                ExpenseFilter.Month -> expense.date.month == now.month && expense.date.year == now.year
                ExpenseFilter.Year -> expense.date.year == now.year
            }
        }

        val thisMonthExpenses = expenses.filter { expense ->
            expense.date.month == now.month && expense.date.year == now.year
        }
        val spentByCategory = thisMonthExpenses
            .groupBy { it.category }
            .mapValues { (_, categoryExpenses) -> categoryExpenses.sumOf { it.amount } }

        val budgetAlerts = budgets
            .filter { budget -> (spentByCategory[budget.category] ?: 0.0) > budget.limitAmount }
            .map { budget -> budget.copy(spentAmount = spentByCategory[budget.category] ?: 0.0) }

        val filteredTotal = filteredExpenses.sumOf { it.amount }
        val topCategory = filteredExpenses
            .groupBy { it.category }
            .maxByOrNull { (_, categoryExpenses) -> categoryExpenses.sumOf { it.amount } }
            ?.key
        val recentExpenses = filteredExpenses
            .sortedByDescending { it.date }
            .take(20)

        DashboardUiState(
            isLoading = false,
            filteredTotal = filteredTotal,
            recentExpenses = recentExpenses,
            topCategory = topCategory,
            selectedFilter = filter,
            budgetAlerts = budgetAlerts,
        )
    }
    .catch { throwable ->
        emit(DashboardUiState(isLoading = false, errorMessage = throwable.message))
    }
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DashboardUiState(),
    )

    fun onFilterSelected(filter: ExpenseFilter) {
        _selectedFilter.value = filter
    }
}
