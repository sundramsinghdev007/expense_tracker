// feature/settings/src/main/java/com/sundram/expense_tracker/settings/SettingsUiState.kt
package com.sundram.expense_tracker.settings

data class SettingsUiState(
    val selectedCurrency: String = "INR",
    val isDarkTheme: Boolean? = null,          // null = follow system
    val notificationsEnabled: Boolean = true,
    val appVersion: String = "",
    val showCurrencyDialog: Boolean = false,
)
