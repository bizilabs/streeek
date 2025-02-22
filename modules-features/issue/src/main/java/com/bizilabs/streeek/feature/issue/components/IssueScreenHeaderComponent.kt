package com.bizilabs.streeek.feature.issue.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.feature.issue.IssueScreenState
import com.bizilabs.streeek.lib.common.helpers.fromHex
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.resources.strings.SafiStringLabels
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.OutlinedRichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueScreenHeaderComponent(
    state: IssueScreenState,
    onClickNavigateBack: () -> Unit,
    onClickCreateIssue: () -> Unit,
    onNavigateToEditIssue: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text =
                            "ISSUE #${state.number}".takeIf { state.number != null }
                                ?: stringResource(SafiStringLabels.newIssue),
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
                    AnimatedVisibility(visible = state.number == null) {
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

                    AnimatedVisibility(visible = state.isIssueAuther == true) {
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
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = state.number,
                label = "animate feedback header",
            ) { id ->
                when {
                    id == null -> {
                        Text(text = "Create Feedback")
                    }

                    state.issueState is FetchState.Success -> {
                        val issue = state.issueState.value
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                        ) {
                            Text(
                                text = "Title",
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Text(text = issue.title)
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.labelLarge,
                            )
                            val state = rememberRichTextState()
                            state.setHtml(html = issue.body)

                            OutlinedRichTextEditor(
                                modifier = Modifier.fillMaxWidth(),
                                contentPadding = PaddingValues(0.dp),
                                readOnly = true,
                                state = state,
                                colors =
                                    RichTextEditorDefaults.outlinedRichTextEditorColors(
                                        containerColor = Color.Transparent,
                                        textColor = MaterialTheme.colorScheme.onBackground,
                                        unfocusedBorderColor = Color.Transparent,
                                    ),
                            )
                            Spacer(modifier = Modifier.padding(8.dp))
                            LazyRow {
                                items(issue.labels) { label ->
                                    Card(
                                        modifier = Modifier.padding(horizontal = 4.dp),
                                        colors =
                                            CardDefaults.cardColors(
                                                containerColor = Color.fromHex(label.color),
                                                contentColor = Color.Black,
                                            ),
                                    ) {
                                        Text(
                                            modifier =
                                                Modifier.padding(
                                                    vertical = 4.dp,
                                                    horizontal = 8.dp,
                                                ),
                                            text = label.name,
                                            style = MaterialTheme.typography.labelLarge,
                                        )
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        Text(text = "Feedback")
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.fillMaxWidth())
        }
    }
}
