// feature/analytics/src/main/java/com/sundram/expense_tracker/analytics/DailySpend.kt
package com.sundram.expense_tracker.analytics

import java.time.LocalDate

data class DailySpend(
    val date: LocalDate,
    val amount: Double,
)
