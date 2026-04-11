// feature/ocr/src/main/java/com/sundram/expense_tracker/ocr/CameraPermissionHandler.kt
package com.sundram.expense_tracker.ocr

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Stateless composable that handles the CAMERA runtime permission.
 *
 * - If permission is already granted, [onPermissionGranted] is composed immediately.
 * - If permission is not yet granted, a system permission dialog is launched and
 *   [onPermissionDenied] is composed until the user grants permission.
 *
 * `var hasPermission` is justified here: it is ephemeral UI state driven by the
 * one-shot result of the system permission dialog, and cannot be expressed as a val.
 */
@Composable
fun CameraPermissionHandler(
    onPermissionGranted: @Composable () -> Unit,
    onPermissionDenied: @Composable () -> Unit,
) {
    val context = LocalContext.current

    // var justified: ephemeral UI state updated by the ActivityResult callback.
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasPermission = granted },
    )

    LaunchedEffect(Unit) {
        if (!hasPermission) {
            launcher.launch(android.Manifest.permission.CAMERA)
        }
    }

    if (hasPermission) {
        onPermissionGranted()
    } else {
        onPermissionDenied()
    }
}
