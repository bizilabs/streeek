package com.bizilabs.streeek.feature.issue.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.feature.issue.IssueScreenState
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.domain.models.LabelDomain
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueScreenLabelsSheet(
    state: IssueScreenState,
    onClickDismissLabelsSheet: () -> Unit,
    onClickAddLabel: (LabelDomain) -> Unit,
    onClickLabelsRetry: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            onClickDismissLabelsSheet()
            scope.launch { sheetState.hide() }
        },
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.padding(24.dp),
                    text = "Labels".uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = state.labelsState,
            ) { result ->
                when (result) {
                    FetchListState.Loading -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                            SafiCircularProgressIndicator(modifier = Modifier.padding(48.dp))
                        }
                    }

                    FetchListState.Empty -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                            SafiInfoSection(
                                icon = Icons.AutoMirrored.Rounded.Label,
                                title = "No Labels",
                                description = "No labels have been created yet.",
                            )
                        }
                    }

                    is FetchListState.Error -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                            SafiInfoSection(
                                icon = Icons.AutoMirrored.Rounded.Label,
                                title = "Error",
                                description = "Could not load labels.",
                            ) {
                                IconButton(onClick = onClickLabelsRetry) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = "retry",
                                    )
                                }
                            }
                        }
                    }

                    is FetchListState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(result.list) {
                                val selected = (state.editIssue?.labels ?: emptyList()).union(state.labels).toList().contains(it)
                                Card(
                                    onClick = { onClickAddLabel(it) },
                                    colors =
                                        CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        ),
                                    shape = RectangleShape,
                                ) {
                                    Row(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Text(
                                            text =
                                                it.name.lowercase()
                                                    .replaceFirstChar { it.uppercase() },
                                        )
                                        Icon(
                                            imageVector =
                                                if (selected) {
                                                    Icons.Rounded.CheckCircle
                                                } else {
                                                    Icons.Outlined.Circle
                                                },
                                            contentDescription = "",
                                            tint =
                                                if (selected) {
                                                    MaterialTheme.colorScheme.success
                                                } else {
                                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                                },
                                        )
                                    }
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.padding(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
