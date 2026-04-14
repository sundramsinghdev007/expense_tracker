// feature/dashboard/src/main/java/com/sundram/expense_tracker/dashboard/component/DashboardSearchBar.kt
package com.sundram.expense_tracker.dashboard.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.sundram.expense_tracker.dashboard.R
import com.sundram.expense_tracker.ui.theme.Dimens

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearchSubmit: (String) -> Unit,
    searchHistory: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onHistoryItemRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }
    val showHistory = isFocused && query.isEmpty() && searchHistory.isNotEmpty()

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(text = stringResource(R.string.dashboard_search_hint)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.dashboard_search_clear_cd),
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearchSubmit(query) }),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingSm)
                .onFocusChanged { isFocused = it.isFocused },
        )

        AnimatedVisibility(visible = showHistory) {
            Column(
                modifier = Modifier.padding(
                    horizontal = Dimens.spacingMd,
                    vertical = Dimens.spacingXs,
                ),
            ) {
                Text(
                    text = stringResource(R.string.dashboard_search_history_title),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = Dimens.spacingXs),
                )
                FlowRow {
                    searchHistory.forEach { item ->
                        InputChip(
                            selected = false,
                            onClick = { onHistoryItemClick(item) },
                            label = { Text(text = item) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = null,
                                    modifier = Modifier.size(Dimens.spacingMd),
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { onHistoryItemRemove(item) },
                                    modifier = Modifier.size(Dimens.spacingLg),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = stringResource(
                                            R.string.dashboard_search_history_remove_cd,
                                            item,
                                        ),
                                        modifier = Modifier.size(Dimens.spacingMd),
                                    )
                                }
                            },
                            modifier = Modifier.padding(end = Dimens.spacingXs),
                        )
                    }
                }
            }
        }
    }
}
