// feature/add-expense/src/main/java/com/sundram/expense_tracker/addexpense/AddExpenseUiState.kt
package com.sundram.expense_tracker.addexpense

import com.sundram.expense_tracker.domain.model.Category
import java.time.LocalDate

data class AddExpenseUiState(
    val expenseId: Long? = null,            // non-null = edit mode
    val title: String = "",
    val amount: String = "",
    val selectedCategory: Category = Category.OTHER,
    val date: LocalDate = LocalDate.now(),
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val validationErrors: Map<String, Int> = emptyMap(), // Int values are @StringRes resource IDs
    val showDeleteDialog: Boolean = false,
) {
    val isEditMode: Boolean get() = expenseId != null
}
