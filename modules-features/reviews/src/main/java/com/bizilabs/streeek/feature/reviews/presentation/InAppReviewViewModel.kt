package com.bizilabs.streeek.feature.reviews.presentation

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizilabs.streeek.feature.reviews.ReviewManagerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InAppReviewViewModel(private val reviewManagerHelper: ReviewManagerHelper) : ViewModel() {
    private val _reviewState = MutableStateFlow<ReviewState>(ReviewState.Idle)
    val reviewState: StateFlow<ReviewState> = _reviewState

    fun requestReview(activity: Activity) {
        viewModelScope.launch {
            _reviewState.value = ReviewState.Loading
            val result = reviewManagerHelper.triggerInAppReview(activity)
            _reviewState.value =
                if (result.isSuccess) {
                    ReviewState.Success
                } else {
                    ReviewState.Error(result.exceptionOrNull()?.localizedMessage ?: "Unknown error")
                }
        }
    }

    sealed class ReviewState {
        data object Idle : ReviewState()

        data object Loading : ReviewState()

        data object Success : ReviewState()

        data class Error(val message: String) : ReviewState()
    }
}
