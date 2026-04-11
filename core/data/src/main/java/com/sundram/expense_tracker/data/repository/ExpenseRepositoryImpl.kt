// core/data/src/main/java/com/sundram/expense_tracker/data/repository/ExpenseRepositoryImpl.kt
package com.sundram.expense_tracker.data.repository

import com.sundram.expense_tracker.data.local.dao.ExpenseDao
import com.sundram.expense_tracker.data.local.entity.toDomain
import com.sundram.expense_tracker.data.local.entity.toEntity
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.repository.ExpenseRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao,
) : ExpenseRepository {

    override fun getAllExpenses(): Flow<List<Expense>> =
        dao.getAllExpenses().map { list -> list.map { it.toDomain() } }

    override fun getExpensesByCategory(category: Category): Flow<List<Expense>> =
        dao.getByCategory(category.name).map { list -> list.map { it.toDomain() } }

    override fun getExpensesByDateRange(start: LocalDate, end: LocalDate): Flow<List<Expense>> =
        dao.getByDateRange(start.toString(), end.toString())
            .map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Expense? =
        dao.getById(id)?.toDomain()

    override suspend fun insert(expense: Expense): Long =
        dao.insert(expense.toEntity())

    override suspend fun update(expense: Expense) {
        dao.update(expense.toEntity())
    }

    override suspend fun delete(expense: Expense) {
        dao.delete(expense.toEntity())
    }
}
