package com.bizilabs.streeek.feature.onboarding

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn
import com.bizilabs.streeek.lib.design.components.SafiCenteredRow
import com.bizilabs.streeek.lib.design.theme.SafiTheme
import com.bizilabs.streeek.lib.resources.SafiResources
import kotlinx.coroutines.launch

val ScreenOnBoarding =
    screenModule {
        register<SharedScreen.OnBoarding> { OnBoardingScreen }
    }

object OnBoardingScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel: OnBoardingScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        OnBoardingScreenContent(
            state = state,
            onClickFinish = screenModel::onClickFinish,
        ) { screen ->
            navigator?.replace(screen)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreenContent(
    state: OnBoardingScreenState,
    onClickFinish: () -> Unit,
    navigate: (Screen) -> Unit,
) {
    if (state.navigateToHome) navigate(rememberScreen(SharedScreen.Tabs))
    if (state.navigateToAuthentication) navigate(rememberScreen(SharedScreen.Authentication))

    val coroutineScope = rememberCoroutineScope()

    Scaffold { innerPadding ->

        val pagerState = rememberPagerState { state.items.size }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            SafiCenteredRow(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    modifier =
                        Modifier
                            .padding(horizontal = 16.dp)
                            .padding(vertical = 24.dp)
                            .size(24.dp),
                    painter = painterResource(SafiResources.Drawables.Logo),
                    contentDescription = "logo",
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
            HorizontalPager(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                state = pagerState,
            ) { index ->

                val item = state.items[index]
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.weight(0.75f)) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(item.drawableId),
                            contentDescription = "on boarding image",
                        )
                    }

                    SafiCenteredColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .weight(0.25f),
                    ) {
                        Text(
                            modifier = Modifier.padding(vertical = 16.dp),
                            text = stringResource(item.title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            text = stringResource(item.description),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            SafiCenteredColumn(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
            ) {
                Row(
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    repeat(pagerState.pageCount) { iteration ->
                        AnimatedContent(
                            label = "animate indicator",
                            targetState = iteration,
                        ) { index ->

                            val isSelected = pagerState.currentPage == index
                            val color =
                                if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onBackground.copy(
                                        alpha = 0.2f,
                                    )
                                }
                            val shape = if (isSelected) RoundedCornerShape(50) else CircleShape

                            Box(
                                modifier =
                                    Modifier
                                        .padding(end = 4.dp)
                                        .clip(shape)
                                        .background(color)
                                        .width(if (isSelected) 24.dp else 12.dp)
                                        .height(12.dp),
                            )
                        }
                    }
                }

                val isLast = pagerState.currentPage == state.items.lastIndex

                Button(
                    modifier = Modifier.padding(vertical = 24.dp),
                    onClick = {
                        if (isLast) {
                            onClickFinish()
                        } else {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage.plus(1))
                            }
                        }
                    },
                ) {
                    Text(text = if (isLast) "Finish" else "Next")
                }
            }
        }
    }
}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun OnBoardingScreenPreview() {
    SafiTheme {
        OnBoardingScreenContent(state = OnBoardingScreenState(), onClickFinish = {}) {}
    }
}
