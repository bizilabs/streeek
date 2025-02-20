package com.bizilabs.streeek.feature.issue.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bizilabs.streeek.feature.issue.IssueScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIssueScreenHeaderComponent(
    state: IssueScreenState,
    onClickNavigateBack: () -> Unit,
    onClickCreateIssue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text("Edit Issue") },
                navigationIcon = {
                    IconButton(onClick = onClickNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "navigate back",
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(visible = true) {
                        IconButton(
                            onClick = onClickCreateIssue,
                            enabled = true,
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
                                tint = Color.White,
                                imageVector = Icons.Default.Check,
                                contentDescription = "Edit feedback",
                            )
                        }
                    }
                },
            )
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}
