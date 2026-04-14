// core/ui/src/main/java/com/sundram/expense_tracker/ui/component/ExpenseTrackerTopBar.kt
package com.sundram.expense_tracker.ui.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sundram.expense_tracker.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrackerTopBar(
    title:    String,
    onBack:   (() -> Unit)? = null,
    actions:  @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier          = modifier,
        title             = { Text(text = title) },
        navigationIcon    = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.ui_back),
                    )
                }
            }
        },
        actions = { actions() },
    )
}
