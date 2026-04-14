// feature/settings/src/main/java/com/sundram/expense_tracker/settings/SettingsViewModel.kt
package com.sundram.expense_tracker.settings

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundram.expense_tracker.domain.usecase.ExportExpensesUseCase
import com.sundram.expense_tracker.settings.data.getCurrencyCode
import com.sundram.expense_tracker.settings.data.getIsDarkTheme
import com.sundram.expense_tracker.settings.data.getNotificationsEnabled
import com.sundram.expense_tracker.settings.data.setCurrencyCode
import com.sundram.expense_tracker.settings.data.setIsDarkTheme
import com.sundram.expense_tracker.settings.data.setNotificationsEnabled
import com.sundram.expense_tracker.settings.data.settingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
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
    private val exportExpensesUseCase: ExportExpensesUseCase,
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
            _uiState.update { it.copy(exportLoading = true, exportError = null) }
            exportExpensesUseCase()
                .mapCatching { csvContent -> writeCsvToDownloads(csvContent) }
                .onSuccess {
                    _uiState.update { it.copy(exportLoading = false, exportSuccess = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(exportLoading = false, exportError = error.message) }
                }
        }
    }

    fun onExportDismiss(): Unit {
        _uiState.update { it.copy(exportSuccess = false, exportError = null) }
    }

    private fun writeCsvToDownloads(csvContent: String) {
        val fileName = "expenses_${LocalDate.now()}.csv"
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
        }
        val resolver = context.contentResolver
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Downloads.EXTERNAL_CONTENT_URI
        }
        val uri = resolver.insert(collection, contentValues)
            ?: error("MediaStore insert returned null — cannot write CSV")
        resolver.openOutputStream(uri)?.use { stream ->
            stream.write(csvContent.toByteArray(Charsets.UTF_8))
        } ?: error("Could not open output stream for CSV file")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }
    }
}
