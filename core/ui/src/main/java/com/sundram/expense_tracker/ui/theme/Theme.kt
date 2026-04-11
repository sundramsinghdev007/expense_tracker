// core/ui/src/main/java/com/sundram/expense_tracker/ui/theme/Theme.kt
package com.sundram.expense_tracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),
    error   = Color(0xFFC62828),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF66BB6A),
    error   = Color(0xFFEF9A9A),
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = ExpenseTypography,
        content     = content,
    )
}
