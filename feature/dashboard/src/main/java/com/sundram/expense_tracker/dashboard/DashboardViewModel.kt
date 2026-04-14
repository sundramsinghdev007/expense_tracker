// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/DashboardViewModel.kt
package com.sundram.expense_tracker.dashboard

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundram.expense_tracker.dashboard.data.addToSearchHistory
import com.sundram.expense_tracker.dashboard.data.getSearchHistory
import com.sundram.expense_tracker.dashboard.data.removeFromSearchHistory
import com.sundram.expense_tracker.domain.model.ExpenseFilter
import com.sundram.expense_tracker.domain.usecase.GetBudgetsUseCase
import com.sundram.expense_tracker.domain.usecase.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val getBudgetsUseCase: GetBudgetsUseCase,
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow<ExpenseFilter>(ExpenseFilter.Month)
    private val _searchQuery = MutableStateFlow("")
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())

    init {
        viewModelScope.launch {
            context.getSearchHistory().collect { _searchHistory.value = it }
        }
    }

    private val _coreState: Flow<DashboardUiState> = combine(
        getExpensesUseCase(),
        getBudgetsUseCase(YearMonth.now()),
        _selectedFilter,
        _searchQuery,
    ) { expenses, budgets, filter, query ->
        val now = LocalDate.now()

        val dateFilteredExpenses = expenses.filter { expense ->
            when (filter) {
                ExpenseFilter.Day -> expense.date == now
                ExpenseFilter.Month -> expense.date.month == now.month && expense.date.year == now.year
                ExpenseFilter.Year -> expense.date.year == now.year
            }
        }

        val visibleExpenses = if (query.isBlank()) {
            dateFilteredExpenses
        } else {
            dateFilteredExpenses.filter { it.title.contains(query, ignoreCase = true) }
        }

        val thisMonthExpenses = expenses.filter { expense ->
            expense.date.month == now.month && expense.date.year == now.year
        }
        val spentByCategory = thisMonthExpenses
            .groupBy { it.category }
            .mapValues { (_, categoryExpenses) -> categoryExpenses.sumOf { it.amount } }
        val visibleCategories = if (query.isBlank()) null else visibleExpenses.map { it.category }.toSet()
        val budgetAlerts = budgets
            .filter { budget -> (spentByCategory[budget.category] ?: 0.0) > budget.limitAmount }
            .filter { budget -> visibleCategories == null || budget.category in visibleCategories }
            .map { budget -> budget.copy(spentAmount = spentByCategory[budget.category] ?: 0.0) }

        DashboardUiState(
            isLoading = false,
            filteredTotal = visibleExpenses.sumOf { it.amount },
            recentExpenses = visibleExpenses.sortedByDescending { it.date }.take(20),
            topCategory = visibleExpenses
                .groupBy { it.category }
                .maxByOrNull { (_, e) -> e.sumOf { it.amount } }
                ?.key,
            selectedFilter = filter,
            budgetAlerts = budgetAlerts,
            searchQuery = query,
        )
    }.catch { throwable ->
        emit(DashboardUiState(isLoading = false, errorMessage = throwable.message))
    }

    val uiState: StateFlow<DashboardUiState> = combine(
        _coreState,
        _searchHistory,
    ) { coreState, history ->
        coreState.copy(searchHistory = history)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = DashboardUiState(),
    )

    fun onFilterSelected(filter: ExpenseFilter) {
        _selectedFilter.value = filter
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onSearchSubmit(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch { context.addToSearchHistory(query) }
    }

    fun onSearchHistoryItemClick(query: String) {
        _searchQuery.value = query
        viewModelScope.launch { context.addToSearchHistory(query) }
    }

    fun onSearchHistoryItemRemove(query: String) {
        viewModelScope.launch { context.removeFromSearchHistory(query) }
    }
}
