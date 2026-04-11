// feature/analytics/src/main/java/com/sundram/expense_tracker/analytics/AnalyticsScreen.kt
package com.sundram.expense_tracker.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AnalyticsContent(
        uiState = uiState,
        onPeriodChange = viewModel::onPeriodChange,
    )
}
