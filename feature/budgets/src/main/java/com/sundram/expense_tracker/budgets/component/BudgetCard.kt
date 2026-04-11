// feature/budgets/src/main/java/com/sundram/expense_tracker/budgets/component/BudgetCard.kt
package com.sundram.expense_tracker.budgets.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.sundram.expense_tracker.budgets.R
import com.sundram.expense_tracker.domain.model.Budget
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun BudgetCard(
    budget: Budget,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        targetValue = budget.progressFraction,
        label = "budget_progress",
    )

    val progressCd = stringResource(
        R.string.budgets_progress_cd,
        (budget.progressFraction * 100).toInt(),
    )

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.minTouchTarget)
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingXs),
    ) {
        Column(
            modifier = Modifier.padding(Dimens.spacingMd),
        ) {
            CategoryAndStatusRow(budget = budget)
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.spacingXs)
                    .semantics { contentDescription = progressCd },
                color = if (budget.isOverBudget) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
            )
            SpentAndLimitRow(budget = budget)
        }
    }
}

@Composable
private fun CategoryAndStatusRow(
    budget: Budget,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${budget.category.emoji} ${budget.category.displayName}",
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = if (budget.isOverBudget) {
                stringResource(R.string.budgets_over_budget)
            } else {
                stringResource(R.string.budgets_remaining_label, "₹%.0f".format(budget.remainingAmount))
            },
            color = if (budget.isOverBudget) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun SpentAndLimitRow(
    budget: Budget,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.budgets_spent_label, "₹%.0f".format(budget.spentAmount)),
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = stringResource(R.string.budgets_limit_label, "₹%.0f".format(budget.limitAmount)),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
