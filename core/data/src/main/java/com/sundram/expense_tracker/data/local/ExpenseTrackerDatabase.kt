// core/data/src/main/java/com/sundram/expense_tracker/data/local/ExpenseTrackerDatabase.kt
package com.sundram.expense_tracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sundram.expense_tracker.data.local.dao.BudgetDao
import com.sundram.expense_tracker.data.local.dao.ExpenseDao
import com.sundram.expense_tracker.data.local.entity.BudgetEntity
import com.sundram.expense_tracker.data.local.entity.ExpenseEntity

@Database(
    entities = [ExpenseEntity::class, BudgetEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class ExpenseTrackerDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
}
