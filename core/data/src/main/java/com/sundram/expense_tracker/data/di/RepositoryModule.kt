// core/data/src/main/java/com/sundram/expense_tracker/data/di/RepositoryModule.kt
package com.sundram.expense_tracker.data.di

import com.sundram.expense_tracker.data.repository.BudgetRepositoryImpl
import com.sundram.expense_tracker.data.repository.ExpenseRepositoryImpl
import com.sundram.expense_tracker.domain.repository.BudgetRepository
import com.sundram.expense_tracker.domain.repository.ExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    abstract fun bindBudgetRepository(impl: BudgetRepositoryImpl): BudgetRepository
}
