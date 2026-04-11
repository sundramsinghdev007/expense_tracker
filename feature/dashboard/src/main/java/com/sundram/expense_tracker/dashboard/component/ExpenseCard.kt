// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/component/ExpenseCard.kt
package com.sundram.expense_tracker.dashboard.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.sundram.expense_tracker.dashboard.R
import com.sundram.expense_tracker.domain.model.Expense
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun ExpenseCard(
    expense: Expense,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.minTouchTarget)
            .padding(
                horizontal = Dimens.spacingMd,
                vertical = Dimens.spacingXs,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingMd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = expense.category.emoji,
                modifier = Modifier.size(Dimens.iconSize),
            )
            Spacer(modifier = Modifier.width(Dimens.spacingMd))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = expense.category.displayName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            val amountCd = String.format(
                stringResource(R.string.dashboard_expense_amount_cd),
                expense.amount,
            )
            Text(
                text = "₹%.2f".format(expense.amount),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.semantics { contentDescription = amountCd },
            )
        }
    }
}
