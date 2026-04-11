// core/domain/src/main/java/com/sundram/expense_tracker/domain/usecase/CheckBudgetAfterExpenseUseCase.kt
package com.sundram.expense_tracker.domain.usecase

import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.repository.BudgetRepository
import com.sundram.expense_tracker.domain.repository.ExpenseRepository
import java.time.YearMonth
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Returns true if the given [category]'s total spending this month
 * now exceeds its budget limit (i.e. the just-saved expense pushed it over).
 * Returns false when no budget exists for the category.
 */
class CheckBudgetAfterExpenseUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val expenseRepository: ExpenseRepository,
) {
    suspend operator fun invoke(category: Category): Boolean {
        val month = YearMonth.now()
        val budget = budgetRepository.getBudgetsForMonth(month).first()
            .find { it.category == category } ?: return false
        val spent = expenseRepository.getAllExpenses().first()
            .filter { it.category == category && it.date.year == month.year && it.date.monthValue == month.monthValue }
            .sumOf { it.amount }
        return spent > budget.limitAmount
    }
}
