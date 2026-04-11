// feature/analytics/src/main/java/com/sundram/expense_tracker/analytics/AnalyticsViewModel.kt
package com.sundram.expense_tracker.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.usecase.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(Period.MONTH)

    val uiState: StateFlow<AnalyticsUiState> = combine(
        getExpensesUseCase(),
        _selectedPeriod,
    ) { expenses, period -> expenses to period }
        .map { (expenses, period) ->
            val filtered = filterByPeriod(expenses, period)
            val totalSpend = filtered.sumOf { it.amount }
            val categoryBreakdown = filtered
                .groupBy { it.category }
                .mapValues { (_, categoryExpenses) -> categoryExpenses.sumOf { e -> e.amount } }
            val dailySpend = filtered
                .groupBy { it.date }
                .map { (date, dayExpenses) -> DailySpend(date, dayExpenses.sumOf { e -> e.amount }) }
                .sortedBy { it.date }
            val trend = computeTrend(dailySpend)
            AnalyticsUiState(
                isLoading = false,
                selectedPeriod = period,
                totalSpend = totalSpend,
                categoryBreakdown = categoryBreakdown,
                dailySpend = dailySpend,
                trend = trend,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AnalyticsUiState(),
        )

    fun onPeriodChange(period: Period) {
        _selectedPeriod.value = period
    }

    private fun filterByPeriod(expenses: List<Expense>, period: Period): List<Expense> {
        val now = LocalDate.now()
        return when (period) {
            Period.WEEK -> {
                val weekStart = now.minusDays(6)
                expenses.filter { it.date >= weekStart && it.date <= now }
            }
            Period.MONTH -> expenses.filter {
                it.date.year == now.year && it.date.month == now.month
            }
            Period.YEAR -> expenses.filter { it.date.year == now.year }
        }
    }

    private fun computeTrend(dailySpend: List<DailySpend>): Trend {
        if (dailySpend.size < 2) return Trend.FLAT
        val midpoint = dailySpend.size / 2
        val firstHalfTotal = dailySpend.take(midpoint).sumOf { it.amount }
        val secondHalfTotal = dailySpend.drop(midpoint).sumOf { it.amount }
        return when {
            secondHalfTotal > firstHalfTotal * 1.1 -> Trend.UP
            secondHalfTotal < firstHalfTotal * 0.9 -> Trend.DOWN
            else -> Trend.FLAT
        }
    }
}
