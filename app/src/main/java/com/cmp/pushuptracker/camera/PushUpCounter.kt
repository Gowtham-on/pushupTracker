package com.cmp.pushuptracker.camera

import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class PushUpCounter {
    private var isDown = false
    private var count = 0

    fun updatePose(pose: Pose, livePreviewViewModel: LivePreviewViewmodel): Int {
        val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)


        if (listOf(leftShoulder, rightShoulder, leftHip, rightHip, leftElbow, rightElbow)
                .any { it == null || it.inFrameLikelihood < 0.5f }
        ) {
            return count
        }

        val noseY = nose!!.position.y
        val leftShoulderY = leftShoulder!!.position.y
        val rightShoulderY = rightShoulder!!.position.y
        val leftHipY = leftHip!!.position.y
        val rightHipY = rightHip!!.position.y

        val avgShoulderY = (leftShoulderY + rightShoulderY) / 2
        val avgHipY = (leftHipY + rightHipY) / 2

        // Define thresholds
        val downThreshold = avgShoulderY + (avgHipY - avgShoulderY) * 0.6
        val upThreshold = avgShoulderY - 50f // little above shoulders

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
        count = 0
    }
}