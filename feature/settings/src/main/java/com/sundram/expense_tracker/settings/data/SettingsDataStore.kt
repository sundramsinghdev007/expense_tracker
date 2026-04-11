// feature/settings/src/main/java/com/sundram/expense_tracker/settings/data/SettingsDataStore.kt
package com.sundram.expense_tracker.settings.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

object SettingsKeys {
    val CURRENCY_CODE         = stringPreferencesKey("currency_code")
    val IS_DARK_THEME         = booleanPreferencesKey("is_dark_theme")
    val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
}

fun DataStore<Preferences>.getCurrencyCode(): Flow<String> =
    data.map { it[SettingsKeys.CURRENCY_CODE] ?: "INR" }

fun DataStore<Preferences>.getIsDarkTheme(): Flow<Boolean?> =
    data.map { it[SettingsKeys.IS_DARK_THEME] }

fun DataStore<Preferences>.getNotificationsEnabled(): Flow<Boolean> =
    data.map { it[SettingsKeys.NOTIFICATIONS_ENABLED] ?: true }

suspend fun DataStore<Preferences>.setCurrencyCode(code: String) {
    edit { it[SettingsKeys.CURRENCY_CODE] = code }
}

suspend fun DataStore<Preferences>.setIsDarkTheme(isDark: Boolean) {
    edit { it[SettingsKeys.IS_DARK_THEME] = isDark }
}

suspend fun DataStore<Preferences>.setNotificationsEnabled(enabled: Boolean) {
    edit { it[SettingsKeys.NOTIFICATIONS_ENABLED] = enabled }
}
