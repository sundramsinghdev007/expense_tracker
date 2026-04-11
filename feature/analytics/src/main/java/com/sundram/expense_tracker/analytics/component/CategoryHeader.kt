// feature/analytics/src/main/java/com/sundram/expense_tracker/analytics/component/CategoryHeader.kt
package com.sundram.expense_tracker.analytics.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.sundram.expense_tracker.analytics.R
import com.sundram.expense_tracker.analytics.Trend

@Composable
fun CategoryHeader(
    totalSpend: Double,
    trend: Trend,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "₹%.2f".format(totalSpend),
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = stringResource(R.string.analytics_total_spend_label),
                style = MaterialTheme.typography.labelSmall,
            )
        }
        Icon(
            imageVector = trendIcon(trend),
            contentDescription = stringResource(trendCdRes(trend)),
            tint = trendColor(trend),
        )
    }
}

private fun trendIcon(trend: Trend): ImageVector = when (trend) {
    Trend.UP -> Icons.Filled.KeyboardArrowUp
    Trend.DOWN -> Icons.Filled.KeyboardArrowDown
    Trend.FLAT -> Icons.Filled.Remove
}

@Composable
private fun trendColor(trend: Trend): Color = when (trend) {
    Trend.UP -> MaterialTheme.colorScheme.error
    Trend.DOWN -> Color(0xFF2E7D32) // green — savings signal
    Trend.FLAT -> MaterialTheme.colorScheme.onSurfaceVariant
}

private fun trendCdRes(trend: Trend): Int = when (trend) {
    Trend.UP -> R.string.analytics_trend_up_cd
    Trend.DOWN -> R.string.analytics_trend_down_cd
    Trend.FLAT -> R.string.analytics_trend_flat_cd
}
