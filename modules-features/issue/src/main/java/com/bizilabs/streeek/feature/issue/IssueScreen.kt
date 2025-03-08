package com.bizilabs.streeek.feature.issue

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Comment
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import com.bizilabs.streeek.feature.issue.components.IssueScreenCreateSection
import com.bizilabs.streeek.feature.issue.components.IssueScreenHeaderComponent
import com.bizilabs.streeek.feature.issue.components.IssueScreenLabelsSheet
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.common.models.FetchState
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiBottomDialog
import com.bizilabs.streeek.lib.design.components.SafiInfoSection
import com.bizilabs.streeek.lib.domain.helpers.toTimeAgo
import com.bizilabs.streeek.lib.domain.models.CommentDomain
import com.bizilabs.streeek.lib.domain.models.LabelDomain
import com.bizilabs.streeek.lib.resources.SafiResources
import dev.jeziellago.compose.markdowntext.MarkdownText

class IssueScreen(val id: Long?) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel = getScreenModel<IssueScreenModel>()
        val state by screenModel.state.collectAsStateWithLifecycle()
        screenModel.onValueChangeId(id)

        LifecycleResumeEffect(Unit) {
            screenModel.refreshIssue()
            onPauseOrDispose { }
        }

        val screenEditIssue = rememberScreen(SharedScreen.EditIssue(id))
        // Handle navigation to a specific issue
        val comments = screenModel.comments.collectAsLazyPagingItems()
        IssueScreenContent(
            state = state,
            comments = comments,
            onClickNavigateBack = { navigator?.pop() },
            onClickCreateIssue = screenModel::onClickCreateIssue,
            onNavigateToEditIssue = { navigator?.push(screenEditIssue) },
            onValueChangeTitle = screenModel::onValueChangeTitle,
            onValueChangeDescription = screenModel::onValueChangeDescription,
            onClickInsertLabel = screenModel::onClickInsertLabel,
            onClickRemoveLabel = screenModel::onClickRemoveLabel,
            onClickOpenLabels = screenModel::onClickOpenLabels,
            onClickLabelsDismissSheet = screenModel::onClickLabelsDismissSheet,
            onClickLabelsRetry = screenModel::onClickLabelsRetry,
            onClickDismissDialog = screenModel::onClickDismissDialog,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssueScreenContent(
    state: IssueScreenState,
    comments: LazyPagingItems<CommentDomain>,
    onClickNavigateBack: () -> Unit,
    onClickCreateIssue: () -> Unit,
    onNavigateToEditIssue: () -> Unit,
    onValueChangeTitle: (String) -> Unit,
    onValueChangeDescription: (String) -> Unit,
    onClickInsertLabel: (LabelDomain) -> Unit,
    onClickRemoveLabel: (LabelDomain) -> Unit,
    onClickOpenLabels: () -> Unit,
    onClickLabelsDismissSheet: () -> Unit,
    onClickLabelsRetry: () -> Unit,
    onClickDismissDialog: () -> Unit,
) {
    if (state.dialogState != null) {
        SafiBottomDialog(state = state.dialogState, onClickDismiss = onClickDismissDialog)
    }

    if (state.isSelectingLabels) {
        IssueScreenLabelsSheet(
            state = state,
            onClickDismissLabelsSheet = onClickLabelsDismissSheet,
            onClickAddLabel = onClickInsertLabel,
            onClickLabelsRetry = onClickLabelsRetry,
        )
    }

    Scaffold(
        topBar = {
            IssueScreenHeaderComponent(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                onClickNavigateBack = onClickNavigateBack,
                onClickCreateIssue = onClickCreateIssue,
                onNavigateToEditIssue = onNavigateToEditIssue,
            )
        },
    ) { innerPadding ->
        AnimatedContent(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            targetState = state.issueNumber,
            label = "animate issue",
        ) { id ->
            when (id) {
                null -> {
                    IssueScreenCreateSection(
                        modifier = Modifier.fillMaxSize(),
                        state = state,
                        onValueChangeTitle = onValueChangeTitle,
                        onValueChangeDescription = onValueChangeDescription,
                        onClickOpenLabels = onClickOpenLabels,
                        onClickRemoveLabel = onClickRemoveLabel,
                    )
                }

                else -> {
                    SafiPagingComponent(
                        modifier = Modifier.fillMaxSize(),
                        data = comments,
                        prependSuccess = {
                            IssuePageHeaderComponent(
                                modifier = Modifier.fillMaxWidth(),
                                state = state,
                            )
                        },
                        refreshEmpty = {
                            SafiInfoSection(
                                icon = Icons.AutoMirrored.Rounded.Comment,
                                title = "No comments",
                                description = "Issue has no comments yet!",
                            )
                        },
                    ) { comment ->
                        SafiCommentItem(comment = comment)
                    }
                }
            }
        }
    }
}

@Composable
fun IssuePageHeaderComponent(
    state: IssueScreenState,
    modifier: Modifier = Modifier,
) {
    val issue = (state.issueState as? FetchState.Success)?.value
    AnimatedVisibility(
        visible = issue != null,
        enter = slideInVertically(),
    ) {
        if (issue != null) {
            Surface(modifier = modifier) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                    ) {
                        Text(
                            text = stringResource(SafiResources.Strings.Labels.Title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        MarkdownText(markdown = issue.title)
                        if (issue.body.isNotBlank()) {
                            Text(
                                modifier = Modifier.padding(top = 8.dp),
                                text = stringResource(SafiResources.Strings.Labels.Description),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                            )
                            MarkdownText(markdown = issue.body)
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun SafiCommentItem(comment: CommentDomain) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(vertical = 8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                AsyncImage(
                    modifier =
                        Modifier
                            .padding(end = 16.dp, top = 16.dp)
                            .size(32.dp)
                            .clip(CircleShape),
                    model = comment.user.url,
                    contentDescription = "avatar image url",
                )

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.primary.copy(0.1f)),
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text =
                                buildString {
                                    append(comment.user.name)
                                    append(" • ")
                                    append(comment.updatedAt.toTimeAgo())
                                },
                            fontSize = MaterialTheme.typography.labelMedium.fontSize,
                        )

                        MarkdownText(
                            modifier = Modifier.fillMaxWidth(),
                            markdown = comment.body,
                        )
                    }
                }
            }
        }
    }
}
