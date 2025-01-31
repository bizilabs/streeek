package com.bizilabs.streeek.lib.design.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp

@Composable
fun SafiCircleDecoratorComponent(dotIndicatorColor: Color = MaterialTheme.colorScheme.primary.copy(0.8f)) {
    Canvas(
        modifier = Modifier.wrapContentSize(),
    ) {
        drawCircle(
            color = dotIndicatorColor,
            radius = 2.dp.toPx(),
            alpha = 1f,
            style = Fill,
        )
    }
}
