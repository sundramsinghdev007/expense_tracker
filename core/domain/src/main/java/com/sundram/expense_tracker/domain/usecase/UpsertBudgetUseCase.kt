// core/domain/src/main/java/com/sundram/expense_tracker/domain/usecase/UpsertBudgetUseCase.kt
package com.sundram.expense_tracker.domain.usecase

import com.sundram.expense_tracker.domain.model.Budget
import com.sundram.expense_tracker.domain.repository.BudgetRepository
import javax.inject.Inject

class UpsertBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository,
) {
    suspend operator fun invoke(budget: Budget) = repository.upsertBudget(budget)
}
