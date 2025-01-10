package com.bizilabs.streeek.feature.issues

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import com.bizilabs.streeek.lib.common.components.paging.SafiPagingComponent
import com.bizilabs.streeek.lib.common.helpers.fromHex
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SearchAction
import com.bizilabs.streeek.lib.design.components.SearchBar
import com.bizilabs.streeek.lib.domain.helpers.toTimeAgo
import com.bizilabs.streeek.lib.domain.models.IssueDomain
import com.bizilabs.streeek.lib.resources.strings.SafiStrings

object IssuesScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenIssue = rememberScreen(SharedScreen.Issue())
        val screenModel = getScreenModel<IssuesScreenModel>()
        val state by screenModel.state.collectAsStateWithLifecycle()
        val issues = screenModel.issues.collectAsLazyPagingItems()
        IssuesScreenContent(
            state = state,
            issues = issues,
            onClickNavigateBack = { navigator?.pop() },
            onClickIssue = screenModel::onClickIssue,
            onClickAddIssue = { navigator?.push(screenIssue) },
        ) { screen -> navigator?.push(screen) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IssuesScreenContent(
    state: IssuesScreenState,
    issues: LazyPagingItems<IssueDomain>,
    onClickNavigateBack: () -> Unit,
    onClickAddIssue: () -> Unit,
    onClickIssue: (IssueDomain) -> Unit,
    navigate: (Screen) -> Unit,
) {

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Track whether to load more data or not
    val shouldFetchMore by remember { mutableStateOf(true) }

    // Filter the issues based on the search query
    val filteredIssues = issues.itemSnapshotList.items.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    // Function to load the next page
    fun fetchNextPageIfNeeded() {
        // Check if the current page has no results, and if we are not already fetching more
        if (filteredIssues.isEmpty() && shouldFetchMore) {
            // Attempt to load the next page
            if (issues.loadState.append !is LoadState.Loading) {
                issues.retry()
            }
        }
    }

    // Fetch the next page if needed
    fetchNextPageIfNeeded()

    // Update navigation when a specific issue is selected
    if (state.issue != null) navigate(rememberScreen(SharedScreen.Issue(id = state.issue.number)))
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        modifier = Modifier.padding(),
                        onClick = onClickNavigateBack,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "navigate back",
                        )
                    }
                },
                title =
                {
                    if (isSearching) {
                        SearchBar(
                            query = searchQuery,
                            onQueryChanged = { searchQuery = it },
                            onClose = {
                                isSearching = false
                                searchQuery = ""
                            }
                        )
                    } else {
                        Text(
                            text = stringResource(SafiStrings.Labels.Issues).uppercase(),
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                },
                actions = { SearchAction(isSearching) { isSearching = it } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onClickAddIssue,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                Icon(imageVector = Icons.Rounded.Add, contentDescription = "add issue")
            }
        },
    ) { innerPadding ->
        SafiPagingComponent(
            modifier =
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            data = issues,
        ) { issue ->

            // Render the filtered issues
            if (filteredIssues.contains(issue)) {
                Column(
                    modifier = Modifier.clickable { onClickIssue(issue) },
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            AsyncImage(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(48.dp)
                                    .clip(CircleShape),
                                model = issue.user.url,
                                contentDescription = "avatar image url",
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Row {
                                    Text(
                                        text = "#${issue.number} • ",
                                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                    )
                                    Text(
                                        text = issue.createdAt.toTimeAgo(),
                                        fontSize = MaterialTheme.typography.labelMedium.fontSize,
                                    )
                                }

                                Text(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    text = issue.title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = FontWeight.Bold,
                                )

                                AnimatedVisibility(
                                    modifier = Modifier.fillMaxWidth(),
                                    visible = issue.labels.isNotEmpty(),
                                ) {
                                    LazyRow {
                                        items(issue.labels) { label ->
                                            Card(
                                                modifier = Modifier.padding(end = 4.dp),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color.fromHex(label.color),
                                                ),
                                            ) {
                                                Text(
                                                    modifier = Modifier.padding(
                                                        vertical = 4.dp,
                                                        horizontal = 8.dp
                                                    ),
                                                    text = label.name,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = Color.Black,
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    HorizontalDivider(modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
