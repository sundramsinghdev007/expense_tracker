// feature/add-expense/src/main/java/com/sundram/expense_tracker/addexpense/component/AmountInputField.kt
package com.sundram.expense_tracker.addexpense.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.sundram.expense_tracker.addexpense.R

@Composable
fun AmountInputField(
    amount: String,
    onAmountChange: (String) -> Unit,
    errorMessage: String?,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = amount,
        onValueChange = onAmountChange,
        label = { Text(stringResource(R.string.add_expense_amount_label)) },
        prefix = { Text("₹") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        isError = errorMessage != null,
        supportingText = errorMessage?.let { error -> { Text(error) } },
        modifier = modifier.fillMaxWidth(),
    )
}
