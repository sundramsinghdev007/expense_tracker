// feature/analytics/src/main/java/com/sundram/expense_tracker/analytics/component/CategoryBreakdownChart.kt
package com.sundram.expense_tracker.analytics.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sundram.expense_tracker.analytics.R
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.ui.component.EmptyState
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun CategoryBreakdownChart(
    categoryBreakdown: Map<Category, Double>,
    modifier: Modifier = Modifier,
) {
    if (categoryBreakdown.isEmpty()) {
        EmptyState(
            message = stringResource(R.string.analytics_no_data),
            emoji = "📊",
            modifier = modifier,
        )
        return
    }

    val sortedEntries = remember(categoryBreakdown) {
        categoryBreakdown.entries.sortedByDescending { it.value }
    }
    val maxValue = remember(categoryBreakdown) {
        categoryBreakdown.values.maxOrNull() ?: 1.0
    }
    val total = remember(categoryBreakdown) {
        categoryBreakdown.values.sum().takeIf { it > 0.0 } ?: 1.0
    }

    val barColor = MaterialTheme.colorScheme.primary

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.analytics_chart_category_title),
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(Dimens.spacingSm))
        sortedEntries.forEach { (category, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${category.emoji} ${category.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    // TODO: move to Dimens.kt — Dimens.categoryLabelWidth
                    modifier = Modifier.width(120.dp),
                    maxLines = 1,
                )
                Spacer(modifier = Modifier.width(Dimens.spacingSm))
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(Dimens.spacingLg),
                ) {
                    val barWidth = (value / maxValue) * size.width
                    drawRect(
                        color = barColor,
                        topLeft = Offset.Zero,
                        size = Size(barWidth.toFloat(), size.height),
                    )
                }
                Spacer(modifier = Modifier.width(Dimens.spacingSm))
                Text(
                    text = "%.0f%%".format(value / total * 100),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spacingXs))
        }
    }
}
