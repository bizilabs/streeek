package com.bizilabs.streeek.feature.reviews

import android.app.Activity
import com.bizilabs.streeek.lib.domain.helpers.isDeviceHuawei
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ReviewManagerHelper(private val reviewManager: ReviewManager) {
    suspend fun triggerInAppReview(activity: Activity): Result<Unit> {
        return runCatching {
            if (isDeviceHuawei()) return Result.failure(Exception("Device OS Won't support In App Reviews"))
            val reviewInfo =
                requestReviewInfo() ?: return Result.failure(Exception("ReviewInfo is null"))
            launchReviewFlow(activity, reviewInfo)
        }.onFailure { exception ->
            logStep("Failed to trigger in-app review $exception")
        }
    }

    private suspend fun requestReviewInfo(): ReviewInfo? =
        suspendCancellableCoroutine { continuation ->
            reviewManager.requestReviewFlow().apply {
                addOnSuccessListener { continuation.resume(it) }
                addOnFailureListener { exception ->
                    logStep("Error requesting review info")
                    continuation.resumeWithException(exception)
                }
                addOnCanceledListener {
                    continuation.cancel()
                }
            }
        }

    private suspend fun launchReviewFlow(
        activity: Activity,
        reviewInfo: ReviewInfo,
    ) {
        suspendCancellableCoroutine { continuation ->
            reviewManager.launchReviewFlow(activity, reviewInfo).apply {
                addOnSuccessListener {
                    logStep("Review flow completed successfully")
                    continuation.resume(Unit)
                }
                addOnFailureListener { exception ->
                    logStep("Error launching review flow")

                    continuation.resumeWithException(exception)
                }
                addOnCanceledListener {
                    logStep("Review flow was cancelled")
                    continuation.cancel()
                }
            }
        }
    }

    private fun logStep(message: String) {
        Timber.tag("IN-APP-REVIEWS").w(message)
    }
}
