package com.bizilabs.streeek.feature.issue.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.bizilabs.streeek.feature.issue.IssueScreenState
import com.bizilabs.streeek.lib.resources.strings.SafiStringLabels

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditIssueScreenHeaderComponent(
    state: IssueScreenState,
    onClickNavigateBack: () -> Unit,
    onClickEditIssue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(SafiStringLabels.editIssue),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Start,
                    )
                },
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
                            onClick = onClickEditIssue,
                            enabled = true,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DoneAll,
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
