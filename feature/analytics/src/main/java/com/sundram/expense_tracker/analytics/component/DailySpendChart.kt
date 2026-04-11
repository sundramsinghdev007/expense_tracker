// feature/analytics/src/main/java/com/sundram/expense_tracker/analytics/component/DailySpendChart.kt
package com.sundram.expense_tracker.analytics.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sundram.expense_tracker.analytics.DailySpend
import com.sundram.expense_tracker.analytics.R
import com.sundram.expense_tracker.ui.component.EmptyState

@Composable
fun DailySpendChart(
    dailySpend: List<DailySpend>,
    modifier: Modifier = Modifier,
) {
    if (dailySpend.size < 2) {
        EmptyState(
            message = stringResource(R.string.analytics_no_data),
            emoji = "📈",
            modifier = modifier,
        )
        return
    }

    val maxAmount = remember(dailySpend) { dailySpend.maxOf { it.amount } }

    // Normalised x/y ratios (0.0–1.0) cached per dailySpend list identity.
    // Each pair is (xRatio, yRatio) for a point on the line.
    val normalisedPoints: List<Pair<Float, Float>> = remember(dailySpend) {
        val lastIndex = (dailySpend.size - 1).toFloat()
        val safeMax = if (maxAmount > 0.0) maxAmount else 1.0
        dailySpend.mapIndexed { index, point ->
            val xRatio = index / lastIndex
            val yRatio = (point.amount / safeMax).toFloat()
            xRatio to yRatio
        }
    }

    val lineColor = MaterialTheme.colorScheme.primary

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            // TODO: move 120.dp to Dimens.kt — Dimens.chartHeight
            .height(120.dp),
    ) {
        val path = Path()
        normalisedPoints.forEachIndexed { index, (xRatio, yRatio) ->
            // y=0 is top of canvas; invert so higher spend = taller bar
            val x = xRatio * size.width
            val y = (1f - yRatio) * size.height
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx()),
        )

        // Draw data-point dots for clarity
        normalisedPoints.forEach { (xRatio, yRatio) ->
            drawCircle(
                color = lineColor,
                radius = 3.dp.toPx(),
                center = Offset(
                    x = xRatio * size.width,
                    y = (1f - yRatio) * size.height,
                ),
            )
        }
    }
}
