package com.bizilabs.streeek.feature.tabs.screens.achievements.component

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme

@Composable
fun ConnectorLeftComponent(
    modifier: Modifier = Modifier,
    lineWidth: Float = 10f,
    isDotted: Boolean = false,
    isNextDotted: Boolean = false,
    levelValue: String = "100",
    isCurrent: Boolean = false,
    levelValueContainerColor: Color = Color.Gray,
    levelValueContentColor: Color = Color.White,
    lineAboveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    lineBelowColor: Color = MaterialTheme.colorScheme.success,
    title: String = "",
    subtitle: String = "",
) {
    val titleSize = MaterialTheme.typography.titleMedium.fontSize
    val subTitleSize = MaterialTheme.typography.bodySmall.fontSize
    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .fillMaxHeight(),
    ) {
        // Starting points and dimensions
        val middleY = size.height * 0.5f
        val bottomY = size.height * 0.8f
        val startX = 0f
        val endX = size.width

        // Circle settings
        val circlePadding = 16.dp.toPx()
        val text = levelValue
        val circleRadius = ((text.length * 20).coerceAtLeast(10) / 2f + circlePadding) * 2 - 24.dp.toPx()
        val adjustedStartX = startX + circleRadius
        val adjustedEndX = endX - circleRadius
        val circleCenterY = middleY - 24.dp.toPx()
        val circleCenterX = adjustedStartX

        // Path for the line above (to the center of the circle)
        val pathAbove =
            Path().apply {
                moveTo(adjustedStartX, 0f)
                lineTo(adjustedStartX, circleCenterY)
            }

        // Draw the line above with its specific color
        drawPath(
            path = pathAbove,
            color = lineAboveColor,
            style =
                Stroke(
                    width = lineWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = if (isNextDotted) PathEffect.dashPathEffect(floatArrayOf(30f, 30f), 0f) else null,
                ),
        )

        // Path for the line below (from the center of the circle)
        val curveOffset = 16.dp.toPx()
        val pathBelow =
            Path().apply {
                moveTo(adjustedStartX, circleCenterY)
                lineTo(adjustedStartX, bottomY - curveOffset)
                quadraticTo(
                    adjustedStartX,
                    bottomY,
                    adjustedStartX + curveOffset,
                    bottomY,
                )

                lineTo(adjustedEndX - curveOffset, bottomY)
                quadraticTo(
                    adjustedEndX,
                    bottomY,
                    adjustedEndX,
                    bottomY + curveOffset,
                )

                lineTo(adjustedEndX, bottomY + curveOffset + 20.dp.toPx())
            }

        // Draw the line below with its specific color
        drawPath(
            path = pathBelow,
            color = lineBelowColor,
            style =
                Stroke(
                    width = lineWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = if (isDotted) PathEffect.dashPathEffect(floatArrayOf(30f, 30f), 0f) else null,
                ),
        )

        // Draw the circle container
        drawCircle(
            color = levelValueContainerColor,
            radius = circleRadius,
            center = Offset(circleCenterX, circleCenterY),
        )

        // Draw the level number text inside the circle
        val textPaint =
            Paint().apply {
                color = levelValueContentColor.toArgb()
                textSize = (circleRadius / 1.5f).coerceAtLeast(24.sp.toPx())
                textAlign = Paint.Align.CENTER
            }

        val textHeight = textPaint.fontMetrics.run { descent - ascent }
        drawContext.canvas.nativeCanvas.drawText(
            text,
            circleCenterX,
            circleCenterY + textHeight / 3f,
            textPaint,
        )

        // Draw the arrow below the circle if isCurrent is true
        if (isCurrent) {
            val arrowWidth = 24.dp.toPx()
            val arrowHeight = 16.dp.toPx()
            val arrowPath =
                Path().apply {
                    // Define the arrow pointing upwards
                    moveTo(circleCenterX - arrowWidth / 2, circleCenterY + circleRadius + 16.dp.toPx())
                    lineTo(circleCenterX + arrowWidth / 2, circleCenterY + circleRadius + 16.dp.toPx())
                    lineTo(circleCenterX, circleCenterY + circleRadius + 16.dp.toPx() - arrowHeight)
                    close()
                }

            drawPath(
                path = arrowPath,
                color = levelValueContainerColor,
            )

            drawCircle(
                color = levelValueContainerColor,
                radius = circleRadius + 4.dp.toPx(),
                center = Offset(circleCenterX, circleCenterY),
                style =
                    Stroke(
                        width = 2.dp.toPx(),
                    ),
            )
        }

        // Calculate the new X position for the text to the right of the circle
        val textXPosition = circleCenterX + circleRadius + 16.dp.toPx()

        // Draw the two texts centered inside the column
        val columnTextHeight = 32.dp.toPx()

        // Draw the first text (top of the column)
        val text1Paint =
            Paint().apply {
                textSize = titleSize.toPx()
                textAlign = Paint.Align.LEFT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = levelValueContainerColor.toArgb()
            }
        val text1PositionY = circleCenterY - (circleRadius - 24) / 2
        drawContext.canvas.nativeCanvas.drawText(
            title,
            textXPosition,
            text1PositionY,
            text1Paint,
        )

        // Draw the second text (bottom of the column)
        val text2Paint =
            Paint().apply {
                textSize = subTitleSize.toPx()
                textAlign = Paint.Align.LEFT
                color = levelValueContainerColor.toArgb()
            }
        val text2PositionY = (text1PositionY + columnTextHeight) - 24
        drawContext.canvas.nativeCanvas.drawText(
            subtitle,
            textXPosition,
            text2PositionY,
            text2Paint,
        )
    }
}

@Composable
fun ConnectorRightComponent(
    modifier: Modifier = Modifier,
    lineWidth: Float = 10f,
    isDotted: Boolean = false,
    isNextDotted: Boolean = false,
    levelValue: String = "100",
    isCurrent: Boolean = false,
    levelValueContainerColor: Color = Color.Gray,
    levelValueContentColor: Color = Color.White,
    lineAboveColor: Color = Color.Gray,
    lineBelowColor: Color = Color.Gray,
    title: String = "",
    subtitle: String = "",
) {
    val titleSize = MaterialTheme.typography.titleMedium.fontSize
    val subTitleSize = MaterialTheme.typography.bodySmall.fontSize

    Canvas(
        modifier =
            modifier
                .fillMaxWidth()
                .fillMaxHeight(),
    ) {
        // Starting points and dimensions
        val middleY = size.height * 0.5f
        val bottomY = size.height * 0.8f
        val startX = size.width
        val endX = 0f

        // Circle settings
        val circlePadding = 16.dp.toPx()
        val text = levelValue
        val circleRadius = ((text.length * 20).coerceAtLeast(10) / 2f + circlePadding) * 2 - 24.dp.toPx()
        val adjustedStartX = startX - circleRadius
        val adjustedEndX = endX + circleRadius
        val circleCenterY = middleY - 24.dp.toPx()
        val circleCenterX = adjustedStartX

        // Path for the line above (from top-right to the center of the circle)
        val pathAbove =
            Path().apply {
                moveTo(adjustedStartX, 0f)
                lineTo(adjustedStartX, circleCenterY)
            }

        // Draw the line above with its specific color
        drawPath(
            path = pathAbove,
            color = lineAboveColor,
            style =
                Stroke(
                    width = lineWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = if (isNextDotted) PathEffect.dashPathEffect(floatArrayOf(30f, 30f), 0f) else null,
                ),
        )

        // Path for the line below (from center of the circle to bottom-left)
        val curveOffset = 16.dp.toPx()
        val pathBelow =
            Path().apply {
                moveTo(adjustedStartX, circleCenterY)
                lineTo(adjustedStartX, bottomY - curveOffset)
                quadraticTo(
                    adjustedStartX,
                    bottomY,
                    adjustedStartX - curveOffset,
                    bottomY,
                )

                lineTo(adjustedEndX + curveOffset, bottomY)
                quadraticTo(
                    adjustedEndX,
                    bottomY,
                    adjustedEndX,
                    bottomY + curveOffset,
                )

                lineTo(adjustedEndX, bottomY + curveOffset + 20.dp.toPx())
            }

        // Draw the line below with its specific color
        drawPath(
            path = pathBelow,
            color = lineBelowColor,
            style =
                Stroke(
                    width = lineWidth,
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                    pathEffect = if (isDotted) PathEffect.dashPathEffect(floatArrayOf(30f, 30f), 0f) else null,
                ),
        )

        // Draw the circle container
        drawCircle(
            color = levelValueContainerColor,
            radius = circleRadius,
            center = Offset(circleCenterX, circleCenterY),
        )

        // Draw the level number text inside the circle
        val textPaint =
            Paint().apply {
                color = levelValueContentColor.toArgb()
                textSize = (circleRadius / 1.5f).coerceAtLeast(24.sp.toPx())
                textAlign = Paint.Align.CENTER
            }

        val textHeight = textPaint.fontMetrics.run { descent - ascent }
        drawContext.canvas.nativeCanvas.drawText(
            text,
            circleCenterX,
            circleCenterY + textHeight / 3f,
            textPaint,
        )

        // Draw the arrow below the circle if isCurrent is true
        if (isCurrent) {
            val arrowWidth = 24.dp.toPx()
            val arrowHeight = 16.dp.toPx()
            val arrowPath =
                Path().apply {
                    // Define the arrow pointing upwards
                    moveTo(circleCenterX - arrowWidth / 2, circleCenterY + circleRadius + 16.dp.toPx()) // Bottom-left of the arrow
                    lineTo(circleCenterX + arrowWidth / 2, circleCenterY + circleRadius + 16.dp.toPx()) // Bottom-right of the arrow
                    lineTo(circleCenterX, circleCenterY + circleRadius + 16.dp.toPx() - arrowHeight) // Tip of the arrow pointing upward
                    close()
                }

            drawPath(
                path = arrowPath,
                color = levelValueContainerColor,
            )

            drawCircle(
                color = levelValueContainerColor,
                radius = circleRadius + 4.dp.toPx(),
                center = Offset(circleCenterX, circleCenterY),
                style =
                    Stroke(
                        width = 2.dp.toPx(),
                    ),
            )
        }

        // Calculate the new X position for the text to the left of the circle
        val textXPosition = circleCenterX - circleRadius - 16.dp.toPx() // Space of 24.dp to the left of the circle

        // Draw the two texts centered inside the column
        val columnTextHeight = 32.dp.toPx()

        // Draw the first text (top of the column)
        val text1Paint =
            Paint().apply {
                textSize = titleSize.toPx()
                textAlign = Paint.Align.RIGHT
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                color = levelValueContainerColor.toArgb()
            }
        val text1PositionY = circleCenterY - (circleRadius - 24) / 2
        drawContext.canvas.nativeCanvas.drawText(
            title,
            textXPosition,
            text1PositionY,
            text1Paint,
        )

        // Draw the second text (bottom of the column)
        val text2Paint =
            Paint().apply {
                textSize = subTitleSize.toPx()
                textAlign = Paint.Align.RIGHT
                color = levelValueContainerColor.toArgb()
            }
        val text2PositionY = (text1PositionY + columnTextHeight) - 24
        drawContext.canvas.nativeCanvas.drawText(
            subtitle,
            textXPosition,
            text2PositionY,
            text2Paint,
        )
    }
}

@Preview
@Composable
private fun ConnectorLineComponentPreview() {
    SafiTheme {
        Scaffold { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxWidth()) {
                ConnectorRightComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    isNextDotted = true,
                    isDotted = true,
                )
                ConnectorLeftComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                    isNextDotted = true,
                    isDotted = true,
                )
                ConnectorRightComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                    isCurrent = true,
                    isNextDotted = true,
                    isDotted = false,
                    levelValueContentColor = MaterialTheme.colorScheme.onSuccess,
                    levelValueContainerColor = MaterialTheme.colorScheme.success,
                )
                ConnectorLeftComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                    isNextDotted = false,
                    isDotted = false,
                    levelValueContentColor = MaterialTheme.colorScheme.onSuccess,
                    levelValueContainerColor = MaterialTheme.colorScheme.success,
                )
            }
        }
    }
}
