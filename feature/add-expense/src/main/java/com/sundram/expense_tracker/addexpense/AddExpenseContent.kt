// feature/add-expense/src/main/java/com/sundram/expense_tracker/addexpense/AddExpenseContent.kt
package com.sundram.expense_tracker.addexpense

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sundram.expense_tracker.addexpense.component.AmountInputField
import com.sundram.expense_tracker.addexpense.component.CategorySelector
import com.sundram.expense_tracker.addexpense.component.DatePickerField
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.ui.component.ExpenseTrackerTopBar
import com.sundram.expense_tracker.ui.theme.Dimens
import java.time.LocalDate

@Composable
fun AddExpenseContent(
    uiState: AddExpenseUiState,
    onTitleChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCategoryChange: (Category) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onNotesChange: (String) -> Unit,
    onSave: () -> Unit,
    onBack: () -> Unit,
    onDeleteClick: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onDeleteDismiss: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ExpenseTrackerTopBar(
                title = stringResource(
                    if (uiState.isEditMode) R.string.add_expense_edit_title
                    else R.string.add_expense_title
                ),
                onBack = onBack,
                actions = {
                    if (uiState.isEditMode) {
                        IconButton(onClick = onDeleteClick, enabled = !uiState.isLoading) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.add_expense_delete_cd),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(Dimens.spacingMd)
                .verticalScroll(rememberScrollState()),
        ) {
            AmountInputField(
                amount = uiState.amount,
                onAmountChange = onAmountChange,
                errorMessage = uiState.validationErrors["amount"]?.let { stringResource(it) },
            )

            Spacer(modifier = Modifier.height(Dimens.spacingSm))

            val titleErrorRes = uiState.validationErrors["title"]
            OutlinedTextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = { Text(stringResource(R.string.add_expense_title_label)) },
                isError = titleErrorRes != null,
                supportingText = titleErrorRes?.let { resId -> { Text(stringResource(resId)) } },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(Dimens.spacingSm))

            CategorySelector(
                selectedCategory = uiState.selectedCategory,
                onCategoryChange = onCategoryChange,
            )

            Spacer(modifier = Modifier.height(Dimens.spacingSm))

            DatePickerField(
                date = uiState.date,
                onDateChange = onDateChange,
            )

            Spacer(modifier = Modifier.height(Dimens.spacingSm))

            OutlinedTextField(
                value = uiState.notes,
                onValueChange = onNotesChange,
                label = { Text(stringResource(R.string.add_expense_notes_label)) },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(Dimens.spacingLg))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading,
            ) {
                Text(
                    stringResource(
                        if (uiState.isEditMode) R.string.add_expense_update
                        else R.string.add_expense_save
                    )
                )
            }
        }
    }

    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = onDeleteDismiss,
            title = { Text(stringResource(R.string.add_expense_delete_confirm_title)) },
            text = { Text(stringResource(R.string.add_expense_delete_confirm_body)) },
            confirmButton = {
                TextButton(
                    onClick = onDeleteConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(stringResource(R.string.add_expense_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = onDeleteDismiss) {
                    Text(stringResource(R.string.add_expense_delete_cancel))
                }
            },
        )
    }
}
