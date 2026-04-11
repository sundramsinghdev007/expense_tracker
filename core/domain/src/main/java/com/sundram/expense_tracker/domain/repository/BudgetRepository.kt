// core/domain/src/main/java/com/sundram/expense_tracker/domain/repository/BudgetRepository.kt
package com.sundram.expense_tracker.domain.repository

import com.sundram.expense_tracker.domain.model.Budget
import java.time.YearMonth
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getBudgetsForMonth(month: YearMonth): Flow<List<Budget>>
    suspend fun upsertBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}
