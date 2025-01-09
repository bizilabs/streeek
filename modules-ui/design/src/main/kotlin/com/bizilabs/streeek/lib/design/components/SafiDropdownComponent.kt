package com.bizilabs.streeek.lib.design.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SafiDropdownComponent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    onClickAction: () -> Unit,
)  {
    Box(
        modifier
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        TextField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            value = value,
            readOnly = true,
            onValueChange = onValueChange,
            label = {
                Text(text = "Visibility")
            },
            trailingIcon = {
                IconButton(onClick = { onClickAction() }) {
                    Icon(Icons.Rounded.KeyboardArrowDown, "")
                }
            },
        )
        // Transparent clickable overlay
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .padding(horizontal = 16.dp)
                    .clickable(onClick = { onClickAction() }),
        )
    }
}
