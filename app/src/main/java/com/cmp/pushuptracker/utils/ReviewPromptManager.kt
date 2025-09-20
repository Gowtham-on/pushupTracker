package com.cmp.pushuptracker.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object ReviewPromptManager {
    suspend fun maybeAskForReview(context: Context) {
        if (!PreferenceUtil.shouldPromptForReview(context)) return

        val activity = context.findActivity()
        if (activity == null) {
            PreferenceUtil.markReviewPrompted(context, completed = false)
            return
        }

        val manager = ReviewManagerFactory.create(context)
        val reviewInfo = manager.requestReviewInfo() ?: run {
            PreferenceUtil.markReviewPrompted(context, completed = false)
            return
        }

        val flowSucceeded = manager.launchReview(activity, reviewInfo)
        PreferenceUtil.markReviewPrompted(context, completed = flowSucceeded)
    }

    private suspend fun ReviewManager.requestReviewInfo(): ReviewInfo? {
        return requestReviewFlow().awaitResult()
    }

    private suspend fun ReviewManager.launchReview(
        activity: Activity,
        reviewInfo: ReviewInfo
    ): Boolean {
        return launchReviewFlow(activity, reviewInfo).awaitSuccess()
    }

    private suspend fun <T> Task<T>.awaitResult(): T? {
        return suspendCancellableCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (continuation.isActive) {
                    continuation.resume(if (task.isSuccessful) task.result else null)
                }
            }
        }
    }

    private suspend fun <T> Task<T>.awaitSuccess(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (continuation.isActive) {
                    continuation.resume(task.isSuccessful)
                }
            }
        }
    }

    private tailrec fun Context.findActivity(): Activity? = when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
