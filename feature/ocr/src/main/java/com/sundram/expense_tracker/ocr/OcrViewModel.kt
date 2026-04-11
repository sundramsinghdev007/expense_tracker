// feature/ocr/src/main/java/com/sundram/expense_tracker/ocr/OcrViewModel.kt
package com.sundram.expense_tracker.ocr

import android.content.Context
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OcrViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val _uiState = MutableStateFlow(OcrUiState())
    val uiState: StateFlow<OcrUiState> = _uiState.asStateFlow()

    /**
     * Processes a CameraX [ImageProxy] through ML Kit text recognition.
     * Closes the proxy when recognition completes (success or failure).
     */
    @ExperimentalGetImage
    fun processImage(imageProxy: ImageProxy): Unit {
        viewModelScope.launch {
            _uiState.update { it.copy(isScanning = true, errorMessage = null) }

            val mediaImage = imageProxy.image ?: run {
                imageProxy.close()
                _uiState.update { it.copy(isScanning = false) }
                return@launch
            }

            val inputImage = InputImage.fromMediaImage(
                mediaImage,
                imageProxy.imageInfo.rotationDegrees,
            )

            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer
                .process(inputImage)
                .addOnSuccessListener { visionText ->
                    val rawText = visionText.text
                    val amount = OcrParser.parseAmount(rawText)
                    val date = OcrParser.parseDate(rawText)
                    val merchant = OcrParser.parseMerchantName(rawText)

                    _uiState.update { state ->
                        state.copy(
                            isScanning = false,
                            extractedTitle = merchant.orEmpty(),
                            extractedAmount = amount?.toString().orEmpty(),
                            extractedDate = date?.toString().orEmpty(),
                            errorMessage = null,
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    _uiState.update { state ->
                        state.copy(
                            isScanning = false,
                            errorMessage = exception.message,
                        )
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    fun clearError(): Unit {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
