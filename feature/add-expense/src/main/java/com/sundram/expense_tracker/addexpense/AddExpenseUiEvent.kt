// feature/add-expense/src/main/java/com/sundram/expense_tracker/addexpense/AddExpenseUiEvent.kt
package com.sundram.expense_tracker.addexpense

import androidx.annotation.StringRes

sealed class AddExpenseUiEvent {
    data object NavigateBack : AddExpenseUiEvent()
    data object Deleted : AddExpenseUiEvent()
    data class ShowSnackbar(@StringRes val messageRes: Int) : AddExpenseUiEvent()
    data class BudgetExceeded(val categoryName: String) : AddExpenseUiEvent()
}
