// core/domain/src/main/java/com/sundram/expense_tracker/domain/model/Budget.kt
package com.sundram.expense_tracker.domain.model

import java.time.YearMonth

data class Budget(
    val id: Long = 0,
    val category: Category,
    val limitAmount: Double,
    val spentAmount: Double,
    val month: YearMonth,
) {
    val remainingAmount: Double get() = limitAmount - spentAmount
    val progressFraction: Float get() = (spentAmount / limitAmount).coerceIn(0.0, 1.0).toFloat()
    val isOverBudget: Boolean get() = spentAmount > limitAmount
}
