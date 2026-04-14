// core/domain/src/main/java/com/sundram/expense_tracker/domain/usecase/UpdateExpenseUseCase.kt
package com.sundram.expense_tracker.domain.usecase

import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.repository.ExpenseRepository
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository,
) {
    suspend operator fun invoke(expense: Expense): Result<Unit> = runCatching {
        repository.update(expense)
    }
}
