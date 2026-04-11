// core/domain/src/main/java/com/sundram/expense_tracker/domain/repository/ExpenseRepository.kt
package com.sundram.expense_tracker.domain.repository

import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getAllExpenses(): Flow<List<Expense>>
    fun getExpensesByCategory(category: Category): Flow<List<Expense>>
    fun getExpensesByDateRange(start: LocalDate, end: LocalDate): Flow<List<Expense>>
    suspend fun getById(id: Long): Expense?
    suspend fun insert(expense: Expense): Long
    suspend fun update(expense: Expense)
    suspend fun delete(expense: Expense)
}
