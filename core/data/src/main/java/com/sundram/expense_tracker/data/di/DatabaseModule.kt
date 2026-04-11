// core/data/src/main/java/com/sundram/expense_tracker/data/di/DatabaseModule.kt
package com.sundram.expense_tracker.data.di

import android.content.Context
import androidx.room.Room
import com.sundram.expense_tracker.data.local.ExpenseTrackerDatabase
import com.sundram.expense_tracker.data.local.dao.BudgetDao
import com.sundram.expense_tracker.data.local.dao.ExpenseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ExpenseTrackerDatabase =
        Room.databaseBuilder(
            context,
            ExpenseTrackerDatabase::class.java,
            "expense_tracker.db",
        ).build()

    @Provides
    fun provideExpenseDao(db: ExpenseTrackerDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideBudgetDao(db: ExpenseTrackerDatabase): BudgetDao = db.budgetDao()
}
