// feature/settings/src/main/java/com/sundram/expense_tracker/settings/component/SettingsRow.kt
package com.sundram.expense_tracker.settings.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.sundram.expense_tracker.ui.theme.Dimens

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    ListItem(
        headlineContent = {
            Text(text = title)
        },
        supportingContent = {
            Text(
                text  = subtitle,
                style = MaterialTheme.typography.labelSmall,
            )
        },
        leadingContent = {
            Icon(
                imageVector        = icon,
                contentDescription = null,
                modifier           = Modifier.size(Dimens.iconSize),
            )
        },
        trailingContent = trailingContent,
        modifier = if (onClick != null) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        },
    )
}
