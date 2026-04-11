// core/data/src/main/java/com/sundram/expense_tracker/data/local/entity/BudgetMappers.kt
package com.sundram.expense_tracker.data.local.entity

import com.sundram.expense_tracker.domain.model.Budget
import com.sundram.expense_tracker.domain.model.Category
import java.time.YearMonth

fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    category = Category.valueOf(category),
    limitAmount = limitAmount,
    spentAmount = spentAmount,
    month = YearMonth.parse(month),
)

fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    category = category.name,
    limitAmount = limitAmount,
    spentAmount = spentAmount,
    month = month.toString(),
)
