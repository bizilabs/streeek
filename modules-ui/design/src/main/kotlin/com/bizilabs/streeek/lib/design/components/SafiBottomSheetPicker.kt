package com.bizilabs.streeek.lib.design.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : Any> SafiBottomSheetPicker(
    selected: T?,
    list: List<T>,
    onDismiss: () -> Unit,
    onItemSelected: (T) -> Unit,
    name: @Composable (T) -> String,
    modifier: Modifier = Modifier,
    title: String = "Select",
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun hideSheet() {
        scope.launch {
            sheetState.hide()
        }
    }

    ModalBottomSheet(
        modifier = Modifier.padding(bottom = 24.dp),
        sheetState = sheetState,
        onDismissRequest = {
            hideSheet()
            onDismiss()
        },
    ) {
        SafiColumnPicker(
            title = title,
            modifier = modifier,
            selected = selected,
            list = list,
            onItemSelected = {
                hideSheet()
                onItemSelected(it)
            },
            item = { value, isSelected, _, block ->
                SafiPickItem(
                    item = value,
                    isSelected = isSelected,
                    onClick = block,
                    text = name.invoke(value),
                )
            },
        )
    }
}
