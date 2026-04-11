// core/ui/src/main/java/com/sundram/expense_tracker/ui/theme/Type.kt
package com.sundram.expense_tracker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val ExpenseTypography = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold,     fontSize = 28.sp),
    titleLarge     = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    bodyLarge      = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 16.sp),
    labelSmall     = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 11.sp),
)
