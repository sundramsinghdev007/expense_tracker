// feature/add-expense/src/main/java/com/sundram/expense_tracker/addexpense/AddExpenseViewModel.kt
package com.sundram.expense_tracker.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundram.expense_tracker.addexpense.R
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.domain.usecase.AddExpenseUseCase
import com.sundram.expense_tracker.domain.usecase.CheckBudgetAfterExpenseUseCase
import com.sundram.expense_tracker.domain.usecase.DeleteExpenseUseCase
import com.sundram.expense_tracker.domain.usecase.GetExpenseByIdUseCase
import com.sundram.expense_tracker.domain.usecase.UpdateExpenseUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val checkBudgetAfterExpenseUseCase: CheckBudgetAfterExpenseUseCase,
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AddExpenseUiEvent>()
    val events: SharedFlow<AddExpenseUiEvent> = _events.asSharedFlow()

    fun loadExpense(id: Long) {
        viewModelScope.launch {
            val expense = getExpenseByIdUseCase(id) ?: return@launch
            _uiState.update {
                it.copy(
                    expenseId        = expense.id,
                    title            = expense.title,
                    amount           = expense.amount.toString(),
                    selectedCategory = expense.category,
                    date             = expense.date,
                    notes            = expense.notes,
                )
            }
        }
    }

    fun onTitleChange(title: String): Unit {
        _uiState.update { it.copy(title = title) }
    }

    fun onAmountChange(amount: String): Unit {
        _uiState.update { it.copy(amount = amount) }
    }

    fun onCategoryChange(category: Category): Unit {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onDateChange(date: LocalDate): Unit {
        _uiState.update { it.copy(date = date) }
    }

    fun onNotesChange(notes: String): Unit {
        _uiState.update { it.copy(notes = notes) }
    }

    fun showDeleteConfirm(): Unit {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun dismissDeleteConfirm(): Unit {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun deleteExpense(): Unit {
        val id = _uiState.value.expenseId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showDeleteDialog = false) }
            val expense = getExpenseByIdUseCase(id) ?: run {
                _uiState.update { it.copy(isLoading = false) }
                return@launch
            }
            runCatching { deleteExpenseUseCase(expense) }
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AddExpenseUiEvent.Deleted)
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AddExpenseUiEvent.ShowSnackbar(R.string.add_expense_error_generic))
                }
        }
    }

    fun saveExpense(): Unit {
        val current = _uiState.value
        val errors = buildValidationErrors(current)

        if (errors.isNotEmpty()) {
            _uiState.update { it.copy(validationErrors = errors) }
            return
        }

        val parsedAmount = current.amount.toDoubleOrNull() ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, validationErrors = emptyMap()) }

            val expense = Expense(
                id       = current.expenseId ?: 0L,
                title    = current.title.trim(),
                amount   = parsedAmount,
                category = current.selectedCategory,
                date     = current.date,
                notes    = current.notes.trim(),
            )

            val result = if (current.isEditMode) {
                updateExpenseUseCase(expense)
            } else {
                addExpenseUseCase(expense).map { }
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSaved = true) }
                    if (checkBudgetAfterExpenseUseCase(expense.category)) {
                        _events.emit(AddExpenseUiEvent.BudgetExceeded(expense.category.displayName))
                    }
                    _events.emit(AddExpenseUiEvent.NavigateBack)
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(AddExpenseUiEvent.ShowSnackbar(R.string.add_expense_error_generic))
                }
        }
    }

    private fun buildValidationErrors(state: AddExpenseUiState): Map<String, Int> {
        val errors = mutableMapOf<String, Int>()
        if (state.title.isBlank()) {
            errors["title"] = R.string.add_expense_validation_title_blank
        }
        val parsedAmount = state.amount.toDoubleOrNull()
        if (parsedAmount == null || parsedAmount <= 0.0) {
            errors["amount"] = R.string.add_expense_validation_amount_invalid
        }
        return errors
    }
}
