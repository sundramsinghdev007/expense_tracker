// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/component/SummaryHeader.kt
package com.sundram.expense_tracker.dashboard.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.sundram.expense_tracker.dashboard.R
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.domain.model.ExpenseFilter
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun SummaryHeader(
    filteredTotal: Double,
    topCategory: Category?,
    selectedFilter: ExpenseFilter,
    modifier: Modifier = Modifier,
) {
    val totalLabel = when (selectedFilter) {
        ExpenseFilter.Day -> stringResource(R.string.dashboard_total_today)
        ExpenseFilter.Month -> stringResource(R.string.dashboard_total_this_month)
        ExpenseFilter.Year -> stringResource(R.string.dashboard_total_this_year)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimens.spacingMd),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingMd),
        ) {
            Text(
                text = totalLabel,
                style = MaterialTheme.typography.labelSmall,
            )
            Spacer(modifier = Modifier.height(Dimens.spacingXs))
            val totalCd = String.format(
                stringResource(R.string.dashboard_amount_cd),
                filteredTotal,
            )
            Text(
                text = "₹%.2f".format(filteredTotal),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.semantics { contentDescription = totalCd },
            )
            if (topCategory != null) {
                Spacer(modifier = Modifier.height(Dimens.spacingSm))
                Text(
                    text = stringResource(R.string.dashboard_top_category_label),
                    style = MaterialTheme.typography.labelSmall,
                )
                Spacer(modifier = Modifier.height(Dimens.spacingXs))
                Text(
                    text = "${topCategory.emoji} ${topCategory.displayName}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
