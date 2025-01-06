package com.bizilabs.streeek.lib.design.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
fun SafiProfileArc(
    progress: Long,
    maxProgress: Long,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.success,
    content: @Composable () -> Unit,
) {
    // Circular Image with a progress bar effect
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            val startAngle = 115f // Start at the top of the circle
            val sweepAngle = (progress.toFloat() / maxProgress) * 315f // Calculate the arc sweep angle

            // Draw background arc (full circle)
            drawArc(
                color = tint.copy(alpha = 0.25f),
                startAngle = startAngle,
                sweepAngle = 315f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )

            // Draw progress arc
            drawArc(
                color = tint,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
            )
        }

        content()
    }
}

@Preview
@Composable
private fun ProfileArcLightPreview() {
    SafiTheme {
        Surface {
            Row(modifier = Modifier.padding(16.dp)) {
                SafiProfileArc(
                    modifier = Modifier.size(150.dp),
                    progress = 200,
                    maxProgress = 500,
                ) {}
            }
        }
    }
}

@Preview
@Composable
private fun ProfileArcDarkPreview() {
    SafiTheme(isDarkThemeEnabled = true) {
        Surface {
            Row(modifier = Modifier.padding(16.dp)) {
                SafiProfileArc(
                    modifier = Modifier.size(150.dp),
                    progress = 200,
                    maxProgress = 500,
                ) {}
            }
        }
    }
}
