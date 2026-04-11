// feature/analytics/src/main/java/com/sundram/expense_tracker/analytics/AnalyticsUiState.kt
package com.sundram.expense_tracker.analytics

import com.sundram.expense_tracker.domain.model.Category

data class AnalyticsUiState(
    val isLoading: Boolean = true,
    val selectedPeriod: Period = Period.MONTH,
    val totalSpend: Double = 0.0,
    val categoryBreakdown: Map<Category, Double> = emptyMap(),
    val dailySpend: List<DailySpend> = emptyList(),
    val trend: Trend = Trend.FLAT,
)
