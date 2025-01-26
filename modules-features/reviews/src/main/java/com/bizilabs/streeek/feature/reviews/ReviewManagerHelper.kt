package com.bizilabs.streeek.feature.reviews

import android.app.Activity
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ReviewManagerHelper(private val reviewManager: ReviewManager) {
    suspend fun triggerInAppReview(activity: Activity): Result<Unit> {
        return try {
            val reviewInfo = requestReviewInfo()
            launchReviewFlow(activity, reviewInfo)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun requestReviewInfo(): ReviewInfo {
        return suspendCancellableCoroutine { continuation ->
            val request = reviewManager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result)
                } else {
                    continuation.resumeWithException(
                        task.exception ?: Exception("Failed to request review flow"),
                    )
                }
            }
        }
    }

    private suspend fun launchReviewFlow(
        activity: Activity,
        reviewInfo: ReviewInfo,
    ) {
        suspendCancellableCoroutine { continuation ->
            val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
            flow.addOnCompleteListener {
                continuation.resume(Unit)
            }
        }
    }
}
