// feature/budgets/src/main/java/com/sundram/expense_tracker/budgets/BudgetsViewModel.kt
package com.sundram.expense_tracker.budgets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundram.expense_tracker.domain.model.Budget
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.usecase.GetBudgetsUseCase
import com.sundram.expense_tracker.domain.usecase.GetExpensesUseCase
import com.sundram.expense_tracker.domain.usecase.UpsertBudgetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class BudgetsViewModel @Inject constructor(
    private val getBudgetsUseCase: GetBudgetsUseCase,
    private val upsertBudgetUseCase: UpsertBudgetUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetsUiState())
    val uiState: StateFlow<BudgetsUiState> = _uiState.asStateFlow()

    private val currentMonth = YearMonth.now()

    init {
        viewModelScope.launch {
            combine(
                getBudgetsUseCase(currentMonth),
                getExpensesUseCase(),
            ) { budgets, expenses ->
                val currentMonthExpenses = expenses.filter { it.date.year == currentMonth.year && it.date.monthValue == currentMonth.monthValue }
                val enrichedBudgets = budgets.map { budget ->
                    val spent = currentMonthExpenses
                        .filter { it.category == budget.category }
                        .fold(0.0) { acc, expense -> acc + expense.amount }
                    budget.copy(spentAmount = spent)
                }
                BudgetsUiState(
                    isLoading = false,
                    budgets = enrichedBudgets,
                    currentMonth = currentMonth,
                    totalBudgeted = enrichedBudgets.fold(0.0) { acc, b -> acc + b.limitAmount },
                    totalSpent = enrichedBudgets.fold(0.0) { acc, b -> acc + b.spentAmount },
                    showAddBudgetSheet = false,
                )
            }.collect { computedState ->
                _uiState.update { current ->
                    computedState.copy(showAddBudgetSheet = current.showAddBudgetSheet)
                }
            }
        }
    }

    fun showAddBudgetSheet(): Unit {
        _uiState.update { it.copy(showAddBudgetSheet = true) }
    }

    fun hideAddBudgetSheet(): Unit {
        _uiState.update { it.copy(showAddBudgetSheet = false) }
    }

    fun upsertBudget(category: Category, limitAmount: Double): Unit {
        viewModelScope.launch {
            upsertBudgetUseCase(
                Budget(
                    category = category,
                    limitAmount = limitAmount,
                    spentAmount = 0.0,
                    month = currentMonth,
                ),
            )
            hideAddBudgetSheet()
        }
    }
}
