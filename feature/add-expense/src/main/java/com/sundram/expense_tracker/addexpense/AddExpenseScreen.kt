// feature/add-expense/src/main/java/com/sundram/expense_tracker/addexpense/AddExpenseScreen.kt
package com.sundram.expense_tracker.addexpense

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val BUDGET_ALERT_CHANNEL_ID = "budget_alerts"

// @SuppressLint: POST_NOTIFICATIONS permission is checked via canNotify before notify() is called.
@SuppressLint("MissingPermission")
@Composable
fun AddExpenseScreen(
    onBack: () -> Unit,
    viewModel: AddExpenseViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is AddExpenseUiEvent.NavigateBack -> onBack()
                is AddExpenseUiEvent.ShowSnackbar ->
                    snackbarHostState.showSnackbar(context.getString(event.messageRes))
                is AddExpenseUiEvent.BudgetExceeded -> {
                    val canNotify = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS,
                        ) == PackageManager.PERMISSION_GRANTED
                    if (canNotify) {
                        NotificationManagerCompat.from(context).notify(
                            event.categoryName.hashCode(),
                            NotificationCompat.Builder(context, BUDGET_ALERT_CHANNEL_ID)
                                .setSmallIcon(R.drawable.ic_budget_alert)
                                .setContentTitle(context.getString(R.string.add_expense_budget_exceeded_title))
                                .setContentText(
                                    context.getString(
                                        R.string.add_expense_budget_exceeded_body,
                                        event.categoryName,
                                    )
                                )
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setAutoCancel(true)
                                .build(),
                        )
                    }
                }
            }
        }
    }

    AddExpenseContent(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onAmountChange = viewModel::onAmountChange,
        onCategoryChange = viewModel::onCategoryChange,
        onDateChange = viewModel::onDateChange,
        onNotesChange = viewModel::onNotesChange,
        onSave = viewModel::saveExpense,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}
