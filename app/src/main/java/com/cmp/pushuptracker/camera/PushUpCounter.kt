package com.cmp.pushuptracker.camera

import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark

class PushUpCounter {
    private val counterEngine = PushUpCounterEngine()
    private var noseAlerter: NoseAlerter? = null
    private var formAlerter: FormAlerter? = null

    fun updatePose(
        pose: Pose,
        livePreviewViewModel: LivePreviewViewmodel,
        isInitialPose: Boolean
    ): Int {
        val alerter = noseAlerter ?: NoseAlerter { msg -> livePreviewViewModel.speak(msg) }
            .also { noseAlerter = it }

        val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
        val isNoseVisible = nose?.inFrameLikelihood?.let { it > 0.5f }

        if (isInitialPose) {
            alerter.alertIfNoseMissing(isNoseVisible)
        }
        if (isNoseVisible == true) {
            if (!livePreviewViewModel.isNoseVisibleInCamera) {
                livePreviewViewModel.setNoseVisible(true)
            }
        } else {
            if (livePreviewViewModel.isNoseVisibleInCamera) {
                livePreviewViewModel.setNoseVisible(false)
            }
        }

        if (isInitialPose) {
            return counterEngine.count
        }

        return updatePoseByElbow(pose, livePreviewViewModel)
    }

    fun resetCounter() {
        counterEngine.reset()
    }


    ////////////////////////////////////////////////////////////////////////
    // Side view

    private fun hasGood(lm: PoseLandmark?, minConf: Float = MIN_LANDMARK_CONFIDENCE) =
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

    private fun armAngleOrNull(
        shoulder: PoseLandmark?,
        elbow: PoseLandmark?,
        wrist: PoseLandmark?
    ): Float? {
        if (!hasGood(shoulder) || !hasGood(elbow) || !hasGood(wrist)) return null
        return angleDeg(shoulder!!, elbow!!, wrist!!)
    }

    private fun bodyLineAngleOrNull(
        shoulder: PoseLandmark?,
        hip: PoseLandmark?,
        knee: PoseLandmark?
    ): Float? {
        if (!hasGood(shoulder) || !hasGood(hip) || !hasGood(knee)) return null
        return angleDeg(shoulder!!, hip!!, knee!!)
    }

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

        val armAngle = listOfNotNull(
            armAngleOrNull(lSh, lEl, lWr),
            armAngleOrNull(rSh, rEl, rWr)
        ).minOrNull() ?: return counterEngine.count

        val bodyLineAngle = listOfNotNull(
            bodyLineAngleOrNull(lSh, lHp, lKn),
            bodyLineAngleOrNull(rSh, rHp, rKn)
        ).average()
            .takeIf { !it.isNaN() }
            ?.toFloat()

        val plankOk = bodyLineAngle == null || bodyLineAngle >= BODY_LINE_ANGLE_MIN
        livePreviewViewModel.updateCounterDebug(counterEngine.phase, armAngle, bodyLineAngle, plankOk)

        if (!plankOk) {
            val alerter = formAlerter ?: FormAlerter { msg -> livePreviewViewModel.speak(msg) }
                .also { formAlerter = it }
            alerter.alert("Keep your body straight")
        }

        val update = counterEngine.update(armAngle)
        livePreviewViewModel.updateCounterDebug(update.phase, armAngle, bodyLineAngle, plankOk)
        if (update.counted) {
            livePreviewViewModel.incrementTotalReps()
        }
        return update.count
    }

    companion object {
        private const val MIN_LANDMARK_CONFIDENCE = 0.5f
        private const val BODY_LINE_ANGLE_MIN = 150f
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

class FormAlerter(
    private val minIntervalMs: Long = 7000L,
    private val speaker: (String) -> Unit,
) {
    private var lastAlertAt = 0L

    fun alert(message: String) {
        val now = System.currentTimeMillis()
        if (now - lastAlertAt >= minIntervalMs) {
            speaker(message)
            lastAlertAt = now
        }
    }
}
