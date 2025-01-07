package com.bizilabs.streeek.feature.issue

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Label
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.models.FetchState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.bizilabs.streeek.lib.common.helpers.fromHex
import com.bizilabs.streeek.lib.common.models.FetchListState
import com.bizilabs.streeek.lib.design.components.SafiBottomDialog
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCircularProgressIndicator
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.design.helpers.success
import com.bizilabs.streeek.lib.domain.models.LabelDomain
import kotlinx.coroutines.launch

class IssueScreen(val id: Long?) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = getScreenModel<IssueScreenModel>()
        screenModel.onValueChangeId(id)
        val state by screenModel.state.collectAsState()
        IssueScreenContent(
            state = state,
            onClickNavigateBack = { navigator?.pop() },
            onClickCreateIssue = screenModel::onClickCreateIssue,
            onValueChangeTitle = screenModel::onValueChangeTitle,
            onValueChangeDescription = screenModel::onValueChangeDescription,
            onClickInsertLabel = screenModel::onClickInsertLabel,
            onClickRemoveLabel = screenModel::onClickRemoveLabel,
            onClickOpenLabels = screenModel::onClickOpenLabels,
            onClickLabelsDismissSheet = screenModel::onClickLabelsDismissSheet,
            onClickLabelsRetry = screenModel::onClickLabelsRetry,
            onClickDismissDialog = screenModel::onClickDismissDialog
        )
    }
}

@Composable
fun IssueScreenContent(
    state: IssueScreenState,
    onClickNavigateBack: () -> Unit,
    onClickCreateIssue: () -> Unit,
    onValueChangeTitle: (String) -> Unit,
    onValueChangeDescription: (String) -> Unit,
    onClickInsertLabel: (LabelDomain) -> Unit,
    onClickRemoveLabel: (LabelDomain) -> Unit,
    onClickOpenLabels: () -> Unit,
    onClickLabelsDismissSheet: () -> Unit,
    onClickLabelsRetry: () -> Unit,
    onClickDismissDialog: () -> Unit,
) {

    if (state.dialogState != null)
        SafiBottomDialog(state = state.dialogState, onClickDismiss = onClickDismissDialog)

    if (state.isSelectingLabels)
        IssueScreenLabelsSheet(
            state = state,
            onClickDismissLabelsSheet = onClickLabelsDismissSheet,
            onClickAddLabel = onClickInsertLabel,
            onClickLabelsRetry = onClickLabelsRetry
        )

    Scaffold(
        topBar = {
            IssueScreenHeaderComponent(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                onClickNavigateBack = onClickNavigateBack,
                onClickCreateIssue = onClickCreateIssue
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            targetState = state.id,
            label = "animate issue"
        ) { id ->
            when (id) {
                null -> {
                    IssueScreenCreateSection(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        onValueChangeTitle = onValueChangeTitle,
                        onValueChangeDescription = onValueChangeDescription,
                        onClickOpenLabels = onClickOpenLabels,
                        onClickRemoveLabel = onClickRemoveLabel
                    )
                }

                else -> {
                    SafiCenteredColumn(modifier = Modifier.fillMaxSize()) {
                        Text(text = id.toString(), style = MaterialTheme.typography.displayLarge)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueScreenHeaderComponent(
    state: IssueScreenState,
    onClickNavigateBack: () -> Unit,
    onClickCreateIssue: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        title = {

            val title = when {
                state.id == null -> "Create Feedback"
                state.issueState is FetchState.Success -> "Feedback"
                else -> ""
            }

            Text(text = title)

        },
        navigationIcon = {
            IconButton(onClick = onClickNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "navigate back"
                )
            }
        },
        actions = {
            if (state.id == null)
                IconButton(
                    onClick = onClickCreateIssue,
                    enabled = state.isCreateActionEnabled,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (state.isCreateActionEnabled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(0.25f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.Send,
                        contentDescription = "create feedback"
                    )
                }
        }
    )
}

@Composable
fun IssueScreenCreateSection(
    state: IssueScreenState,
    onValueChangeTitle: (String) -> Unit,
    onValueChangeDescription: (String) -> Unit,
    onClickOpenLabels: () -> Unit,
    onClickRemoveLabel: (LabelDomain) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.title,
            onValueChange = onValueChangeTitle,
            shape = RectangleShape,
            placeholder = {
                Text(text = "Title")
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            )
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            value = state.description,
            onValueChange = onValueChangeDescription,
            shape = RectangleShape,
            placeholder = {
                Text(text = "Description (optional)")
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background
            )
        )
        HorizontalDivider()
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Labels"
            )
            LazyRow(
                modifier = Modifier.weight(1f)
            ) {
                items(state.labels) { label ->
                    Card(
                        modifier = Modifier.padding(horizontal = 4.dp),
                        onClick = { onClickRemoveLabel(label) },
                        colors =
                        CardDefaults.cardColors(
                            containerColor = Color.fromHex(label.color),
                            contentColor = Color.Black,
                        ),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
                                text = label.name,
                                style = MaterialTheme.typography.labelLarge,
                            )
                            Icon(
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .size(12.dp),
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "remove label"
                            )
                        }

                    }
                }
            }
            IconButton(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                onClick = onClickOpenLabels
            ) {
                Icon(
                    imageVector = if (state.isSelectingLabels) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.KeyboardArrowUp,
                    contentDescription = "add label"
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueScreenLabelsSheet(
    state: IssueScreenState,
    onClickDismissLabelsSheet: () -> Unit,
    onClickAddLabel: (LabelDomain) -> Unit,
    onClickLabelsRetry: () -> Unit
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
                    style = MaterialTheme.typography.titleMedium
                )
            }
            AnimatedContent(
                modifier = Modifier.fillMaxWidth(),
                targetState = state.labelsState
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
                                description = "No labels have been created yet."
                            )
                        }
                    }

                    is FetchListState.Error -> {
                        SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                            SafiInfoSection(
                                icon = Icons.AutoMirrored.Rounded.Label,
                                title = "Error",
                                description = "Could not load labels."
                            ) {
                                IconButton(onClick = onClickLabelsRetry) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = "retry"
                                    )
                                }
                            }
                        }
                    }

                    is FetchListState.Success -> {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(result.list) {
                                val selected = state.labels.contains(it)
                                Card(
                                    onClick = { onClickAddLabel(it) },
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                                    ),
                                    shape = RectangleShape
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = it.name.lowercase()
                                                .replaceFirstChar { it.uppercase() }
                                        )
                                        Icon(
                                            imageVector = if (selected)
                                                Icons.Rounded.CheckCircle
                                            else
                                                Icons.Outlined.Circle,
                                            contentDescription = "",
                                            tint = if (selected)
                                                MaterialTheme.colorScheme.success
                                            else
                                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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
