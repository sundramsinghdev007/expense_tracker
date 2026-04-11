// core/domain/src/main/java/com/sundram/expense_tracker/domain/model/ExpenseFilter.kt
package com.sundram.expense_tracker.domain.model

sealed class ExpenseFilter {
    data object Day : ExpenseFilter()
    data object Month : ExpenseFilter()
    data object Year : ExpenseFilter()
}
