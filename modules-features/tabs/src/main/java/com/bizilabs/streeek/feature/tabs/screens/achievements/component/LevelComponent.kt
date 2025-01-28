package com.bizilabs.streeek.feature.tabs.screens.achievements.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.design.helpers.onSuccess
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.domain.extensions.asRank
import com.bizilabs.streeek.lib.domain.helpers.SystemLocalDateTime
import com.bizilabs.streeek.lib.domain.models.LevelDomain

private val Long.isEven
    get() = (this % 2 == 0L)

@Composable
fun LevelComponent(
    current: LevelDomain,
    accountLevel: LevelDomain,
    modifier: Modifier = Modifier,
    points: Long = 0,
) {
    val isCurrent = accountLevel.number == current.number
    val currentIsHigher = current.number > accountLevel.number
    val currentIsLower = current.number < accountLevel.number

    val difference = current.number - accountLevel.number

    val isDotted = currentIsHigher
    val isNextDotted = difference >= 0L

    val lineAboveColor =
        when {
            isNextDotted -> MaterialTheme.colorScheme.onBackground
            else -> MaterialTheme.colorScheme.success
        }

    val lineBelowColor =
        when {
            isCurrent or currentIsLower -> MaterialTheme.colorScheme.success
            else -> MaterialTheme.colorScheme.onBackground
        }

    val (containerColor, contentColor) =
        when {
            isCurrent or currentIsLower -> MaterialTheme.colorScheme.success to MaterialTheme.colorScheme.onSuccess
            else -> MaterialTheme.colorScheme.onBackground to MaterialTheme.colorScheme.background
        }

    val title =
        when {
            difference <= 1L -> current.name.lowercase().replaceFirstChar { it.uppercase() }
            else -> "Unlock Previous Level"
        }
    val subtitle =
        when {
            difference <= 0L -> "${current.minPoints - 1} EXP"
            difference == 1L -> "${current.maxPoints - points} EXP to go"
            else -> "Locked"
        }

    Column(modifier = modifier) {
        when (current.number.isEven) {
            true -> {
                ConnectorRightComponent(
                    modifier = Modifier.fillMaxWidth(),
                    isCurrent = isCurrent,
                    levelValue = current.number.asRank(),
                    title = title,
                    subtitle = subtitle,
                    isDotted = isDotted,
                    isNextDotted = isNextDotted,
                    lineAboveColor = lineAboveColor,
                    lineBelowColor = lineBelowColor,
                    levelValueContainerColor = containerColor,
                    levelValueContentColor = contentColor,
                )
            }

            false -> {
                ConnectorLeftComponent(
                    modifier = Modifier.fillMaxWidth(),
                    isCurrent = isCurrent,
                    levelValue = current.number.asRank(),
                    title = title,
                    subtitle = subtitle,
                    isDotted = isDotted,
                    isNextDotted = isNextDotted,
                    lineAboveColor = lineAboveColor,
                    lineBelowColor = lineBelowColor,
                    levelValueContainerColor = containerColor,
                    levelValueContentColor = contentColor,
                )
            }
        }
    }
}

@Preview
@Composable
private fun LevelComponentPreview() {
    SafiTheme {
        Surface {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                LevelComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                    current = sampleLevels[4],
                    accountLevel = sampleLevels[2],
                )
                LevelComponent(
                    points = 769,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                    current = sampleLevels[3],
                    accountLevel = sampleLevels[2],
                )
                LevelComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                    current = sampleLevels[2],
                    accountLevel = sampleLevels[2],
                )
                LevelComponent(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                    current = sampleLevels[1],
                    accountLevel = sampleLevels[2],
                )
            }
        }
    }
}

val sampleLevels =
    listOf(
        LevelDomain(
            id = 1L,
            name = "Beginner",
            number = 1L,
            maxPoints = 100L,
            minPoints = 0L,
            createdAt = SystemLocalDateTime,
        ),
        LevelDomain(
            id = 2L,
            name = "Intermediate",
            number = 2L,
            maxPoints = 250L,
            minPoints = 101L,
            createdAt = SystemLocalDateTime,
        ),
        LevelDomain(
            id = 3L,
            name = "Advanced",
            number = 3L,
            maxPoints = 500L,
            minPoints = 251L,
            createdAt = SystemLocalDateTime,
        ),
        LevelDomain(
            id = 4L,
            name = "Expert",
            number = 4L,
            maxPoints = 1000L,
            minPoints = 501L,
            createdAt = SystemLocalDateTime,
        ),
        LevelDomain(
            id = 5L,
            name = "Master",
            number = 5L,
            maxPoints = 2000L,
            minPoints = 1001L,
            createdAt = SystemLocalDateTime,
        ),
    )
