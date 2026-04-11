// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/component/FilterChipRow.kt
package com.sundram.expense_tracker.dashboard.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.sundram.expense_tracker.dashboard.R
import com.sundram.expense_tracker.domain.model.ExpenseFilter
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun FilterChipRow(
    selectedFilter: ExpenseFilter,
    onFilterSelected: (ExpenseFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
    ) {
        FilterChip(
            selected = selectedFilter is ExpenseFilter.Day,
            onClick = { onFilterSelected(ExpenseFilter.Day) },
            label = { Text(text = stringResource(R.string.dashboard_filter_day)) },
        )
        FilterChip(
            selected = selectedFilter is ExpenseFilter.Month,
            onClick = { onFilterSelected(ExpenseFilter.Month) },
            label = { Text(text = stringResource(R.string.dashboard_filter_month)) },
        )
        FilterChip(
            selected = selectedFilter is ExpenseFilter.Year,
            onClick = { onFilterSelected(ExpenseFilter.Year) },
            label = { Text(text = stringResource(R.string.dashboard_filter_year)) },
        )
    }
}
