package com.bizilabs.streeek.feature.onboarding

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.repositories.AuthenticationRepository
import com.bizilabs.streeek.lib.domain.repositories.PreferenceRepository
import com.bizilabs.streeek.lib.resources.SafiResources
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

val FeatureModuleOnBoarding =
    module {
        factory {
            OnBoardingScreenModel(
                preferenceRepository = get(),
                authenticationRepository = get(),
            )
        }
    }

data class OnBoardingScreenItem(
    val title: Int,
    val description: Int,
    val drawableId: Int,
)

val onboardingItems =
    listOf(
        OnBoardingScreenItem(
            title = SafiResources.Strings.Labels.OnBoardingTitle1,
            description = SafiResources.Strings.Messages.OnBoardingDescription2,
            drawableId = SafiResources.Drawables.OnBoarding1,
        ),
        OnBoardingScreenItem(
            title = SafiResources.Strings.Labels.OnBoardingTitle2,
            description = SafiResources.Strings.Messages.OnBoardingDescription2,
            drawableId = SafiResources.Drawables.OnBoarding2,
        ),
        OnBoardingScreenItem(
            title = SafiResources.Strings.Labels.OnBoardingTitle3,
            description = SafiResources.Strings.Messages.OnBoardingDescription3,
            drawableId = SafiResources.Drawables.OnBoarding3,
        ),
    )

data class OnBoardingScreenState(
    val items: List<OnBoardingScreenItem> = onboardingItems,
    val navigateToAuthentication: Boolean = false,
    val navigateToHome: Boolean = false,
)

class OnBoardingScreenModel(
    private val preferenceRepository: PreferenceRepository,
    private val authenticationRepository: AuthenticationRepository,
) : StateScreenModel<OnBoardingScreenState>(OnBoardingScreenState()) {
    fun onClickFinish() {
        screenModelScope.launch {
            preferenceRepository.updateUserHasOnBoarded(hasOnBoarded = true)
            val authentication = authenticationRepository.authenticated.first()
            mutableState.update {
                it.copy(
                    navigateToAuthentication = !authentication,
                    navigateToHome = authentication,
                )
            }
        }
    }
}
