// feature/settings/src/main/java/com/sundram/expense_tracker/settings/SettingsViewModel.kt
package com.sundram.expense_tracker.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundram.expense_tracker.settings.data.getCurrencyCode
import com.sundram.expense_tracker.settings.data.getIsDarkTheme
import com.sundram.expense_tracker.settings.data.getNotificationsEnabled
import com.sundram.expense_tracker.settings.data.setCurrencyCode
import com.sundram.expense_tracker.settings.data.setIsDarkTheme
import com.sundram.expense_tracker.settings.data.setNotificationsEnabled
import com.sundram.expense_tracker.settings.data.settingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val dataStore = context.settingsDataStore

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                dataStore.getCurrencyCode(),
                dataStore.getIsDarkTheme(),
                dataStore.getNotificationsEnabled(),
            ) { currency, dark, notif ->
                Triple(currency, dark, notif)
            }.collect { (currency, dark, notif) ->
                _uiState.update {
                    it.copy(
                        selectedCurrency     = currency,
                        isDarkTheme          = dark,
                        notificationsEnabled = notif,
                        appVersion           = getAppVersion(),
                    )
                }
            }
        }
    }

    private fun getAppVersion(): String =
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: ""
        }.getOrDefault("")

    fun onCurrencyChange(code: String): Unit {
        viewModelScope.launch {
            dataStore.setCurrencyCode(code)
            hideDialog()
        }
    }

    fun onDarkThemeChange(isDark: Boolean): Unit {
        viewModelScope.launch {
            dataStore.setIsDarkTheme(isDark)
        }
    }

    fun onNotificationsChange(enabled: Boolean): Unit {
        viewModelScope.launch {
            dataStore.setNotificationsEnabled(enabled)
        }
    }

    fun showCurrencyDialog(): Unit {
        _uiState.update { it.copy(showCurrencyDialog = true) }
    }

    fun hideDialog(): Unit {
        _uiState.update { it.copy(showCurrencyDialog = false) }
    }

    fun onExportCsv(): Unit {
        viewModelScope.launch {
            // TODO: implement CSV export
        }
    }
}
