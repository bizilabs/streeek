package com.bizilabs.streeek.feature.reviews.presentation

import android.app.Activity
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.feature.reviews.ReviewManagerHelper
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class InAppReviewModel(private val reviewManagerHelper: ReviewManagerHelper) :
    StateScreenModel<InAppReviewModel.InAppReviewState>(
        InAppReviewState(),
    ) {
    private val _reviewState = MutableStateFlow<ReviewState>(ReviewState.Idle)
    val reviewState: StateFlow<ReviewState> = _reviewState

    fun requestReview(activity: Activity) {
        screenModelScope.launch {
            _reviewState.value = ReviewState.Loading
            val result = reviewManagerHelper.triggerInAppReview(activity)
            _reviewState.value =
                if (result.isSuccess) {
                    logStep("Successfully Launched In App Review")
                    ReviewState.Success
                } else {
                    logStep("Error  Launching In App Review")
                    ReviewState.Error(result.exceptionOrNull()?.localizedMessage ?: "Unknown error")
                }
        }
    }

    data class InAppReviewState(
        val hasPassedNavigationArgument: Boolean = false,
        val name: String? = null,
        val leaderboard: LeaderboardDomain? = null,
    )

    private fun logStep(s: String) {
        Timber.tag("IN-APPREVIEWS").d(s)
    }

    sealed class ReviewState {
        data object Idle : ReviewState()

        data object Loading : ReviewState()

        data object Success : ReviewState()

        data class Error(val message: String) : ReviewState()
    }
}
