package com.bizilabs.streeek.feature.issue

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.feature.issue.components.EditIssueScreenHeaderComponent
import com.bizilabs.streeek.feature.issue.components.IssueScreenEditSection
import com.bizilabs.streeek.feature.issue.components.IssueScreenLabelsSheet
import com.bizilabs.streeek.lib.design.components.SafiBottomDialog
import com.bizilabs.streeek.lib.domain.models.LabelDomain
import timber.log.Timber

class IssueEditScreen(val id: Long? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current

        val screenModel = getScreenModel<IssueScreenModel>()
        val state by screenModel.state.collectAsStateWithLifecycle()
        screenModel.onValueChangeId(id)

        IssueEditScreenContent(
            state = state,
            onClickNavigateBack = { navigator?.pop() },
            onClickEditIssue = screenModel::onClickEditIssue,
            onEditValueChangeTitle = screenModel::onEditValueChangeTitle,
            onEditValueChangeDescription = screenModel::onEditValueChangeDescription,
            onClickInsertLabel = screenModel::onClickInsertLabel,
            onClickRemoveLabel = screenModel::onClickRemoveEditLabel,
            onClickOpenLabels = screenModel::onClickOpenLabels,
            onClickLabelsDismissSheet = screenModel::onClickLabelsDismissSheet,
            onClickLabelsRetry = screenModel::onClickLabelsRetry,
            onClickDismissDialog = {
                screenModel.onClickDismissDialog()
                navigator?.pop()
            },
        )
    }
}

@Composable
fun IssueEditScreenContent(
    state: IssueScreenState,
    onClickNavigateBack: () -> Unit,
    onClickEditIssue: () -> Unit,
    onEditValueChangeTitle: (String) -> Unit,
    onEditValueChangeDescription: (String) -> Unit,
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
            EditIssueScreenHeaderComponent(
                state = state,
                modifier = Modifier.fillMaxWidth(),
                onClickNavigateBack = onClickNavigateBack,
                onClickEditIssue = onClickEditIssue,
            )
        },
    ) { innerPadding ->
        AnimatedContent(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            targetState = state.number,
            label = "animate issue",
        ) { id ->
            Timber.d("Id: -->$id")
            IssueScreenEditSection(
                modifier = Modifier.fillMaxSize(),
                state = state,
                onValueChangeTitle = onEditValueChangeTitle,
                onValueChangeDescription = onEditValueChangeDescription,
                onClickOpenLabels = onClickOpenLabels,
                onClickRemoveLabel = onClickRemoveLabel,
            )
        }
    }
}
