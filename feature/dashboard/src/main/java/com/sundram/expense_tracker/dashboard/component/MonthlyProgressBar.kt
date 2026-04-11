// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/component/MonthlyProgressBar.kt
package com.sundram.expense_tracker.dashboard.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun MonthlyProgressBar(
    spent: Double,
    budget: Double,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(modifier = Modifier.height(Dimens.spacingXs))
        LinearProgressIndicator(
            progress = { (spent / budget).coerceIn(0.0, 1.0).toFloat() },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
