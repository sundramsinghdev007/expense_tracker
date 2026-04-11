// core/data/src/main/java/com/sundram/expense_tracker/data/local/entity/ExpenseMappers.kt
package com.sundram.expense_tracker.data.local.entity

import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import java.time.LocalDate

fun ExpenseEntity.toDomain(): Expense = Expense(
    id = id,
    title = title,
    amount = amount,
    category = Category.valueOf(category),
    date = LocalDate.parse(date),
    notes = notes,
    receiptUri = receiptUri,
)

fun Expense.toEntity(): ExpenseEntity = ExpenseEntity(
    id = id,
    title = title,
    amount = amount,
    category = category.name,
    date = date.toString(),
    notes = notes,
    receiptUri = receiptUri,
)
