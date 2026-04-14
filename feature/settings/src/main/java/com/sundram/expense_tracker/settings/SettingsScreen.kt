// feature/settings/src/main/java/com/sundram/expense_tracker/settings/SettingsScreen.kt
package com.sundram.expense_tracker.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsContent(
        uiState               = uiState,
        onCurrencyClick       = viewModel::showCurrencyDialog,
        onDarkThemeChange     = viewModel::onDarkThemeChange,
        onNotificationsChange = viewModel::onNotificationsChange,
        onExportCsv           = viewModel::onExportCsv,
        onExportDismiss       = viewModel::onExportDismiss,
        onCurrencySelected    = viewModel::onCurrencyChange,
        onDismissDialog       = viewModel::hideDialog,
    )
}
