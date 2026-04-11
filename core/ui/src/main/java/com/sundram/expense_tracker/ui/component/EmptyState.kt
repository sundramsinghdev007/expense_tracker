// core/ui/src/main/java/com/sundram/expense_tracker/ui/component/EmptyState.kt
package com.sundram.expense_tracker.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun EmptyState(
    message:  String,
    emoji:    String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier            = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text  = emoji,
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(Dimens.spacingSm))
        Text(
            text  = message,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
