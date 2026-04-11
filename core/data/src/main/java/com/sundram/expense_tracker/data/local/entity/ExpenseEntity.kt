// core/data/src/main/java/com/sundram/expense_tracker/data/local/entity/ExpenseEntity.kt
package com.sundram.expense_tracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String,
    val notes: String = "",
    val receiptUri: String? = null,
)
