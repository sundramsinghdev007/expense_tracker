// feature/add-expense/src/main/java/com/sundram/expense_tracker/addexpense/component/CategorySelector.kt
package com.sundram.expense_tracker.addexpense.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sundram.expense_tracker.domain.model.Category
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun CategorySelector(
    selectedCategory: Category,
    onCategoryChange: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacingSm),
    ) {
        items(Category.entries, key = { it.name }) { category ->
            FilterChip(
                selected = category == selectedCategory,
                onClick = { onCategoryChange(category) },
                label = { Text("${category.emoji} ${category.displayName}") },
                modifier = Modifier.heightIn(min = Dimens.minTouchTarget),
            )
        }
    }
}
