// core/domain/src/main/java/com/sundram/expense_tracker/domain/usecase/DeleteExpenseUseCase.kt
package com.sundram.expense_tracker.domain.usecase

import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.repository.ExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository,
) {
    suspend operator fun invoke(expense: Expense) = repository.delete(expense)
}
