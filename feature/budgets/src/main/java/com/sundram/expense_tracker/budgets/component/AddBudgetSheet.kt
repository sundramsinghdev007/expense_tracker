// feature/budgets/src/main/java/com/sundram/expense_tracker/budgets/component/AddBudgetSheet.kt
package com.sundram.expense_tracker.budgets.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.sundram.expense_tracker.budgets.R
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.ui.theme.Dimens

/**
 * Bottom sheet for creating a new monthly budget.
 *
 * Local [var] state for [selectedCategory] and [amountText] is justified here as
 * ephemeral form state that never leaves this composable (CLAUDE.md §6.2 exception).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetSheet(
    onDismiss: () -> Unit,
    onSave: (Category, Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    // var justified: ephemeral form state that does not leave this composable
    var selectedCategory by remember { mutableStateOf(Category.OTHER) }
    // var justified: ephemeral form state that does not leave this composable
    var amountText by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingMd),
        ) {
            Text(
                text = stringResource(R.string.budgets_add_title),
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(Dimens.spacingMd))
            Text(
                text = stringResource(R.string.budgets_category_label),
                style = MaterialTheme.typography.labelMedium,
            )
            Spacer(modifier = Modifier.height(Dimens.spacingXs))
            BudgetCategorySelector(
                selectedCategory = selectedCategory,
                onCategoryChange = { selectedCategory = it },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(Dimens.spacingMd))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text(stringResource(R.string.budgets_limit_amount_label)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            Spacer(modifier = Modifier.height(Dimens.spacingLg))
            Button(
                onClick = {
                    amountText.toDoubleOrNull()?.let { amount ->
                        onSave(selectedCategory, amount)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.budgets_save))
            }
            Spacer(modifier = Modifier.height(Dimens.spacingMd))
        }
    }
}
