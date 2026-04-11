// core/domain/src/main/java/com/sundram/expense_tracker/domain/usecase/GetBudgetsUseCase.kt
package com.sundram.expense_tracker.domain.usecase

import com.sundram.expense_tracker.domain.model.Budget
import com.sundram.expense_tracker.domain.repository.BudgetRepository
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class GetBudgetsUseCase @Inject constructor(
    private val repository: BudgetRepository,
) {
    operator fun invoke(month: YearMonth): Flow<List<Budget>> =
        repository.getBudgetsForMonth(month)
}
