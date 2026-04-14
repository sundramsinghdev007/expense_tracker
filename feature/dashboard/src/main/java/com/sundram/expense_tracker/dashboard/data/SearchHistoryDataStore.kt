// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/data/SearchHistoryDataStore.kt
package com.sundram.expense_tracker.dashboard.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.searchHistoryStore by preferencesDataStore(name = "dashboard_search_history")
private val KEY_HISTORY = stringPreferencesKey("search_history")
private const val SEPARATOR = "|||"
private const val MAX_HISTORY = 5

fun Context.getSearchHistory(): Flow<List<String>> =
    searchHistoryStore.data.map { prefs ->
        prefs[KEY_HISTORY]
            ?.split(SEPARATOR)
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }

suspend fun Context.addToSearchHistory(query: String) {
    val trimmed = query.trim()
    if (trimmed.isBlank()) return
    searchHistoryStore.edit { prefs ->
        val current = prefs[KEY_HISTORY]
            ?.split(SEPARATOR)
            ?.filter { it.isNotBlank() }
            ?: emptyList()
        val updated = (listOf(trimmed) + current.filter { it != trimmed }).take(MAX_HISTORY)
        prefs[KEY_HISTORY] = updated.joinToString(SEPARATOR)
    }
}

suspend fun Context.removeFromSearchHistory(query: String) {
    searchHistoryStore.edit { prefs ->
        val current = prefs[KEY_HISTORY]
            ?.split(SEPARATOR)
            ?.filter { it.isNotBlank() }
            ?: emptyList()
        prefs[KEY_HISTORY] = current.filter { it != query }.joinToString(SEPARATOR)
    }
}
