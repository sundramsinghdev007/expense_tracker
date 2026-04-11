// feature/settings/src/main/java/com/sundram/expense_tracker/settings/component/CurrencyPickerDialog.kt
package com.sundram.expense_tracker.settings.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sundram.expense_tracker.settings.R
import com.sundram.expense_tracker.ui.theme.Dimens

private val SUPPORTED_CURRENCIES = listOf("INR", "USD", "EUR", "GBP")

@Composable
fun CurrencyPickerDialog(
    selectedCurrency: String,
    onCurrencySelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.settings_currency_picker_title))
        },
        text = {
            Column {
                SUPPORTED_CURRENCIES.forEach { code ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCurrencySelected(code) }
                            .padding(vertical = Dimens.spacingSm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected  = code == selectedCurrency,
                            onClick   = { onCurrencySelected(code) },
                            modifier  = Modifier.size(Dimens.minTouchTarget),
                        )
                        Text(text = code)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.settings_close))
            }
        },
    )
}
