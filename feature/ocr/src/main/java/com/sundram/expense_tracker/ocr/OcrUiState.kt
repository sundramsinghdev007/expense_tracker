// feature/ocr/src/main/java/com/sundram/expense_tracker/ocr/OcrUiState.kt
package com.sundram.expense_tracker.ocr

data class OcrUiState(
    val isScanning: Boolean = false,
    val extractedTitle: String = "",
    val extractedAmount: String = "",
    val extractedDate: String = "",
    val errorMessage: String? = null,
)
