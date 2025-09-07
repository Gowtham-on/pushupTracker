package com.cmp.pushuptracker.camera

import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class PushUpCounter {
    private var isDown = false
    private var count = 0

    // Create it lazily the first time we see a ViewModel
    private var noseAlerter: NoseAlerter? = null

    fun updatePose(
        pose: Pose,
        livePreviewViewModel: LivePreviewViewmodel,
        isInitialPose: Boolean
    ): Int {
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


    ////////////////////////////////////////////////////////////////////////
    // Side view

    private fun hasGood(lm: PoseLandmark?, minConf: Float = 0.5f) =
        lm != null && lm.inFrameLikelihood >= minConf

    private fun angleDeg(a: PoseLandmark, b: PoseLandmark, c: PoseLandmark): Float {
        // Angle at B formed by BA and BC, using 3D for robustness
        val bax = a.position3D.x - b.position3D.x
        val bay = a.position3D.y - b.position3D.y
        val baz = a.position3D.z - b.position3D.z

        val bcx = c.position3D.x - b.position3D.x
        val bcy = c.position3D.y - b.position3D.y
        val bcz = c.position3D.z - b.position3D.z

        val dot = bax * bcx + bay * bcy + baz * bcz
        val magBA = kotlin.math.sqrt(bax * bax + bay * bay + baz * baz)
        val magBC = kotlin.math.sqrt(bcx * bcx + bcy * bcy + bcz * bcz)
        if (magBA == 0f || magBC == 0f) return 180f

        val cos = (dot / (magBA * magBC)).coerceIn(-1f, 1f)
        return Math.toDegrees(kotlin.math.acos(cos).toDouble()).toFloat()
    }

    // Tunables
    private val DOWN_ANGLE_MAX = 80f     // arms bent enough
    private val UP_ANGLE_MIN = 160f      // arms straight enough

    fun updatePoseByElbow(pose: Pose, livePreviewViewModel: LivePreviewViewmodel): Int {
        val lSh = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rSh = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val lEl = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rEl = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val lWr = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rWr = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val lHp = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rHp = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val lKn = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rKn = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)

        // Require key arm landmarks
        if (listOf(lSh, rSh, lEl, rEl, lWr, rWr).any { !hasGood(it) }) {
            return count
        }

        val leftAngle = angleDeg(lSh!!, lEl!!, lWr!!)
        val rightAngle = angleDeg(rSh!!, rEl!!, rWr!!)
        val armAngle = minOf(leftAngle, rightAngle)

        // Optional: plank integrity check (shoulder-hip-knee ~ straight)
        val plankOk = if (hasGood(lHp) && hasGood(lKn) && hasGood(rHp) && hasGood(rKn)) {
            val leftTorso = angleDeg(lSh, lHp!!, lKn!!)
            val rightTorso = angleDeg(rSh, rHp!!, rKn!!)
            ((leftTorso + rightTorso) / 2f) >= 155f // warn if hip sags
        } else true

        if (!plankOk) {
            // Say once or throttle via your NoseAlerter pattern
            livePreviewViewModel.speak("Keep your body straight")
        }

        // State machine using arm angle only
        if (!isDown && armAngle <= DOWN_ANGLE_MAX) {
            isDown = true
        }
        if (isDown && armAngle >= UP_ANGLE_MIN) {
            isDown = false
            livePreviewViewModel.incrementTotalReps()
            count++
        }
        return count
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
