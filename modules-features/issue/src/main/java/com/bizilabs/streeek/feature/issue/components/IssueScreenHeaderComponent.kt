package com.bizilabs.streeek.feature.issue.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.bizilabs.streeek.feature.issue.IssueScreenState
import com.bizilabs.streeek.lib.design.components.SafiTopBarHeader
import com.bizilabs.streeek.lib.resources.strings.SafiStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueScreenHeaderComponent(
    state: IssueScreenState,
    onClickNavigateBack: () -> Unit,
    onClickCreateIssue: () -> Unit,
    onNavigateToEditIssue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onClickNavigateBack) {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "navigate back",
                )
            }
        },
        title = {
            Column(
                verticalArrangement = Arrangement.Center,
            ) {
                val title =
                    when (state.issueNumber) {
                        null -> stringResource(SafiStrings.Labels.NewFeedback)
                        else -> {
                            when (state.editIssue) {
                                null -> stringResource(SafiStrings.Labels.Feedback)
                                else -> stringResource(SafiStrings.Labels.Feedback)
                            }
                        }
                    }

                SafiTopBarHeader(title = title)
            }
        },
        actions = {
            AnimatedVisibility(visible = state.issueNumber == null) {
                IconButton(
                    onClick = onClickCreateIssue,
                    enabled = state.isCreateActionEnabled,
                    colors =
                        IconButtonDefaults.iconButtonColors(
                            contentColor =
                                if (state.isCreateActionEnabled) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface.copy(0.25f)
                                },
                        ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "create feedback",
                    )
                }
            }
            AnimatedVisibility(visible = state.isIssueAuthor == true) {
                IconButton(
                    onClick = onNavigateToEditIssue,
                    enabled = true,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit feedback",
                    )
                }
            }
        },
    )
}

@Preview
@Composable
private fun IssueScreenHeaderComponentPreview() {
    Scaffold(
        topBar = {
            IssueScreenHeaderComponent(
                modifier = Modifier.fillMaxWidth(),
                state = IssueScreenState(),
                onClickCreateIssue = {},
                onClickNavigateBack = {},
                onNavigateToEditIssue = {},
            )
        },
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Text(text = "Preview")
        }
    }
}
