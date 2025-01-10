package com.bizilabs.streeek.lib.design.components

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color

@Composable
fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onClose: () -> Unit,
    placeholder: String = "Search issue...",
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
    TextField(
        value = query,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
            )
        },
        onValueChange = onQueryChanged,
        placeholder = { Text(placeholder) },
        trailingIcon = {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear search",
                )
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
        modifier = modifier.fillMaxWidth().focusRequester(focusRequester),
    )
}

@Composable
fun SearchAction(
    isSearching: Boolean,
    onToggleSearch: (Boolean) -> Unit,
) {
    if (!isSearching) {
        IconButton(onClick = { onToggleSearch(true) }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
            )
        }
    }
}
