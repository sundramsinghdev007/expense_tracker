// core/domain/src/main/java/com/sundram/expense_tracker/domain/usecase/ExportExpensesUseCase.kt
package com.sundram.expense_tracker.domain.usecase

import com.sundram.expense_tracker.domain.repository.ExpenseRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class ExportExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository,
) {
    suspend operator fun invoke(): Result<String> = runCatching {
        val expenses = repository.getAllExpenses().first()
        buildString {
            appendLine("id,date,title,category,amount,notes")
            expenses.forEach { expense ->
                appendLine(
                    "${expense.id}," +
                    "${expense.date}," +
                    "\"${expense.title.replace("\"", "\"\"")}\"," +
                    "${expense.category.name}," +
                    "${expense.amount}," +
                    "\"${expense.notes.replace("\"", "\"\"")}\""
                )
            }
        }
    }
}
