// core/domain/src/main/java/com/sundram/expense_tracker/domain/model/Expense.kt
package com.sundram.expense_tracker.domain.model

import java.time.LocalDate

data class Expense(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: Category,
    val date: LocalDate,
    val notes: String = "",
    val receiptUri: String? = null,
)
