// core/domain/src/main/java/com/sundram/expense_tracker/domain/usecase/AddExpenseUseCase.kt
package com.sundram.expense_tracker.domain.usecase

import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository,
) {
    suspend operator fun invoke(expense: Expense): Result<Long> {
        if (expense.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Title cannot be blank"))
        }
        if (expense.amount <= 0) {
            return Result.failure(IllegalArgumentException("Amount must be positive"))
        }
        return runCatching { repository.insert(expense) }
    }
}
