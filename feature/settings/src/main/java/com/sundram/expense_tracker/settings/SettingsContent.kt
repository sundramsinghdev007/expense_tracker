// feature/settings/src/main/java/com/sundram/expense_tracker/settings/SettingsContent.kt
package com.sundram.expense_tracker.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sundram.expense_tracker.settings.component.CurrencyPickerDialog
import com.sundram.expense_tracker.settings.component.SettingsRow
import com.sundram.expense_tracker.ui.component.ExpenseTrackerTopBar
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    onCurrencyClick: () -> Unit,
    onDarkThemeChange: (Boolean) -> Unit,
    onNotificationsChange: (Boolean) -> Unit,
    onExportCsv: () -> Unit,
    onExportDismiss: () -> Unit,
    onCurrencySelected: (String) -> Unit,
    onDismissDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val exportSuccessMessage = stringResource(R.string.settings_export_success)
    val exportErrorPrefix = stringResource(R.string.settings_export_error)

    LaunchedEffect(uiState.exportSuccess) {
        if (uiState.exportSuccess) {
            snackbarHostState.showSnackbar(exportSuccessMessage)
            onExportDismiss()
        }
    }

    LaunchedEffect(uiState.exportError) {
        val error = uiState.exportError
        if (error != null) {
            snackbarHostState.showSnackbar("$exportErrorPrefix $error")
            onExportDismiss()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ExpenseTrackerTopBar(
                title = stringResource(R.string.settings_title),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(Dimens.spacingMd),
        ) {
            SettingsRow(
                icon     = Icons.Filled.AttachMoney,
                title    = stringResource(R.string.settings_currency_title),
                subtitle = uiState.selectedCurrency,
                onClick  = onCurrencyClick,
            )

            SettingsRow(
                icon     = Icons.Filled.DarkMode,
                title    = stringResource(R.string.settings_theme_title),
                subtitle = themeSubtitle(uiState.isDarkTheme),
                trailingContent = {
                    Switch(
                        checked           = uiState.isDarkTheme ?: false,
                        onCheckedChange   = onDarkThemeChange,
                    )
                },
            )

            SettingsRow(
                icon     = Icons.Filled.Notifications,
                title    = stringResource(R.string.settings_notifications_title),
                subtitle = if (uiState.notificationsEnabled) {
                    stringResource(R.string.settings_notifications_on)
                } else {
                    stringResource(R.string.settings_notifications_off)
                },
                trailingContent = {
                    Switch(
                        checked         = uiState.notificationsEnabled,
                        onCheckedChange = onNotificationsChange,
                    )
                },
            )

            SettingsRow(
                icon     = Icons.Filled.FileDownload,
                title    = stringResource(R.string.settings_export_title),
                subtitle = stringResource(R.string.settings_export_subtitle),
                onClick  = if (uiState.exportLoading) null else onExportCsv,
                trailingContent = if (uiState.exportLoading) {
                    { CircularProgressIndicator(modifier = Modifier.size(Dimens.iconSize)) }
                } else {
                    null
                },
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = Dimens.spacingMd),
            )

            SettingsRow(
                icon     = Icons.Filled.Info,
                title    = stringResource(R.string.settings_version_title),
                subtitle = uiState.appVersion,
            )
        }

        if (uiState.showCurrencyDialog) {
            CurrencyPickerDialog(
                selectedCurrency   = uiState.selectedCurrency,
                onCurrencySelected = onCurrencySelected,
                onDismiss          = onDismissDialog,
            )
        }
    }
}

@Composable
private fun themeSubtitle(isDarkTheme: Boolean?): String = when (isDarkTheme) {
    true  -> stringResource(R.string.settings_theme_dark)
    false -> stringResource(R.string.settings_theme_light)
    null  -> stringResource(R.string.settings_theme_system)
}
