// feature/analytics/src/main/java/com/sundram/expense_tracker/analytics/component/PeriodSelector.kt
package com.sundram.expense_tracker.analytics.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sundram.expense_tracker.analytics.Period
import com.sundram.expense_tracker.analytics.R
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun PeriodSelector(
    selectedPeriod: Period,
    onPeriodChange: (Period) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
    ) {
        Period.entries.forEach { period ->
            FilterChip(
                selected = period == selectedPeriod,
                onClick = { onPeriodChange(period) },
                label = { Text(stringResource(periodLabelRes(period))) },
            )
        }
    }
}

private fun periodLabelRes(period: Period): Int = when (period) {
    Period.WEEK -> R.string.analytics_period_week
    Period.MONTH -> R.string.analytics_period_month
    Period.YEAR -> R.string.analytics_period_year
}
