// feature/ocr/src/main/java/com/sundram/expense_tracker/ocr/OcrScanScreen.kt
package com.sundram.expense_tracker.ocr

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundram.expense_tracker.ui.component.ErrorState
import com.sundram.expense_tracker.ui.component.ExpenseTrackerTopBar
import com.sundram.expense_tracker.ui.component.LoadingIndicator

/**
 * Stateful screen composable for OCR receipt scanning.
 *
 * Auto-navigates to the add-expense flow once both [OcrUiState.extractedTitle]
 * and [OcrUiState.extractedAmount] are non-blank.
 */
@Composable
fun OcrScanScreen(
    onNavigateToAddExpense: (title: String, amount: String, date: String) -> Unit,
    onBack: () -> Unit,
    viewModel: OcrViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Auto-navigate once meaningful data has been extracted.
    LaunchedEffect(uiState.extractedTitle, uiState.extractedAmount) {
        if (uiState.extractedTitle.isNotBlank() && uiState.extractedAmount.isNotBlank()) {
            onNavigateToAddExpense(
                uiState.extractedTitle,
                uiState.extractedAmount,
                uiState.extractedDate,
            )
        }
    }

    Scaffold(
        topBar = {
            ExpenseTrackerTopBar(
                title = stringResource(R.string.ocr_title),
                onBack = onBack,
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            CameraPermissionHandler(
                onPermissionGranted = {
                    OcrCameraPreview(
                        onImageCaptured = viewModel::processImage,
                        modifier = Modifier.fillMaxSize(),
                    )
                },
                onPermissionDenied = {
                    ErrorState(
                        message = stringResource(R.string.ocr_permission_denied),
                        retryLabel = stringResource(R.string.ocr_retry),
                        onRetry = {},
                        modifier = Modifier.fillMaxSize(),
                    )
                },
            )

            if (uiState.isScanning) {
                LoadingIndicator(modifier = Modifier.fillMaxSize())
            }

            uiState.errorMessage?.let { message ->
                ErrorState(
                    message = message,
                    retryLabel = stringResource(R.string.ocr_retry),
                    onRetry = viewModel::clearError,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
