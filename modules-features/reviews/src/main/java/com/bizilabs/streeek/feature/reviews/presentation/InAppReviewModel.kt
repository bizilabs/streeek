package com.bizilabs.streeek.feature.reviews.presentation

import android.app.Activity
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.feature.reviews.ReviewManagerHelper
import com.bizilabs.streeek.lib.domain.models.LeaderboardDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class InAppReviewModel(private val reviewManagerHelper: ReviewManagerHelper) :
    StateScreenModel<InAppReviewModel.InAppReviewState>(
        InAppReviewState(),
    ) {
    private val _reviewState = MutableStateFlow<ReviewState>(ReviewState.Idle)
    val reviewState: StateFlow<ReviewState> = _reviewState

    fun requestReview(activity: Activity) {
        screenModelScope.launch {
            _reviewState.update { ReviewState.Loading }
            try {
                val result =
                    withContext(Dispatchers.IO) { reviewManagerHelper.triggerInAppReview(activity) }
                if (result.isSuccess) {
                    logStep("Successfully Launched In-App Review")
                    _reviewState.update { ReviewState.Success }
                } else {
                    val errorMessage = result.exceptionOrNull()?.localizedMessage ?: "Unknown error"
                    logStep("Error Launching In-App Review: $errorMessage")
                    _reviewState.update { ReviewState.Error(errorMessage) }
                }
            } catch (e: Exception) {
                val errorMessage = e.localizedMessage ?: "Unexpected error"
                logStep("Exception in In-App Review: $errorMessage")
                _reviewState.update { ReviewState.Error(errorMessage) }
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
