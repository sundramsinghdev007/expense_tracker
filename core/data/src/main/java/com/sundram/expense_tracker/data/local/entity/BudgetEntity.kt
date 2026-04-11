// core/data/src/main/java/com/sundram/expense_tracker/data/local/entity/BudgetEntity.kt
package com.sundram.expense_tracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String,
    val limitAmount: Double,
    val spentAmount: Double,
    val month: String,
)
