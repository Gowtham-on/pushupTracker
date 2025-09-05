package com.cmp.pushuptracker.camera

import android.util.Log
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class PushUpCounter {
    private var isDown = false
    private var count = 0

    // Create it lazily the first time we see a ViewModel
    private var noseAlerter: NoseAlerter? = null

    fun updatePose(pose: Pose, livePreviewViewModel: LivePreviewViewmodel, isInitialPose: Boolean): Int {
        // Ensure alerter exists (now we have access to the viewmodel)
        val alerter = noseAlerter ?: NoseAlerter { msg -> livePreviewViewModel.speak(msg) }
            .also { noseAlerter = it }

        val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)

        val isNoseVisible = nose?.inFrameLikelihood?.let { it > 0.5f }

        // Speak if nose isn’t visible (throttled), then bail to avoid using nose!!
        alerter.alertIfNoseMissing(isNoseVisible)

        if (isNoseVisible != true) {
            if (livePreviewViewModel.isNoseVisibleInCamera) {
                livePreviewViewModel.setNoseVisible(false)
            }
            return count
        } else if (!livePreviewViewModel.isNoseVisibleInCamera) {
            livePreviewViewModel.setNoseVisible(true)
        }

        if (isInitialPose) {
            return count
        }

        // Bail if any other critical landmarks are missing/low confidence
        if (listOf(leftShoulder, rightShoulder, leftHip, rightHip, leftElbow, rightElbow)
                .any { it == null || it.inFrameLikelihood < 0.5f }
        ) {
            return count
        }

        // Safe !! because we returned above if nose wasn't good
        val noseY = nose!!.position.y
        val leftShoulderY = leftShoulder!!.position.y
        val rightShoulderY = rightShoulder!!.position.y
        val leftHipY = leftHip!!.position.y
        val rightHipY = rightHip!!.position.y

        val avgShoulderY = (leftShoulderY + rightShoulderY) / 2f
        val avgHipY = (leftHipY + rightHipY) / 2f

        // Thresholds as Float (avoid Double math)
        val downThreshold = avgShoulderY + (avgHipY - avgShoulderY) * 0.6f
        val upThreshold = avgShoulderY - 50f

        if (!isDown && noseY > downThreshold) {
            isDown = true
        }
        if (isDown && noseY < upThreshold) {
            isDown = false
            livePreviewViewModel.incrementTotalReps()
            count++
        }
        return count
    }

    fun resetCounter() {
        isDown = false
        count = 0
    }
}

class NoseAlerter(
    private val minIntervalMs: Long = 7000L,
    private val speaker: (String) -> Unit,

    ) {
    private var lastAlertAt = 0L

    fun alertIfNoseMissing(isVisible: Boolean?) {
        if (isVisible == true) return
        val now = System.currentTimeMillis()
        if (now - lastAlertAt >= minIntervalMs) {
            speaker("Please bring your face into view")
            lastAlertAt = now
        }
    }
}
