// core/data/src/main/java/com/sundram/expense_tracker/data/repository/BudgetRepositoryImpl.kt
package com.sundram.expense_tracker.data.repository

import com.sundram.expense_tracker.data.local.dao.BudgetDao
import com.sundram.expense_tracker.data.local.entity.toDomain
import com.sundram.expense_tracker.data.local.entity.toEntity
import com.sundram.expense_tracker.domain.model.Budget
import com.sundram.expense_tracker.domain.repository.BudgetRepository
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl @Inject constructor(
    private val dao: BudgetDao,
) : BudgetRepository {

    override fun getBudgetsForMonth(month: YearMonth): Flow<List<Budget>> =
        dao.getBudgetsByMonth(month.toString()).map { list -> list.map { it.toDomain() } }

    override suspend fun upsertBudget(budget: Budget) {
        dao.upsert(budget.toEntity())
    }

    override suspend fun deleteBudget(budget: Budget) {
        dao.delete(budget.toEntity())
    }
}
