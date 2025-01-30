package com.bizilabs.streeek.lib.design.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun SafiSearchComponent(
    modifier: Modifier = Modifier,
    searchParam: String,
    onSearchParamChanged: (String) -> Unit,
    onClickClearSearch: () -> Unit,
    placeholder: String = "Search item ...",
) {
    TextField(
        modifier =
            modifier
                .fillMaxWidth(),
        value = searchParam,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
            )
        },
        onValueChange = onSearchParamChanged,
        placeholder = { Text(placeholder) },
        trailingIcon = {
            AnimatedVisibility(visible = searchParam.isNotEmpty()) {
                IconButton(onClick = onClickClearSearch) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                    )
                }
            }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.titleSmall,
        colors =
            TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
    )
}
