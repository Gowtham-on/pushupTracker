package com.cmp.pushuptracker.mlKit.posedetector

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.cmp.pushuptracker.mlKit.utils.GraphicOverlay
import com.cmp.pushuptracker.mlKit.utils.GraphicOverlay.Graphic
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.CurrentPhase
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.lang.Float
import kotlin.Boolean
import kotlin.String
import kotlin.math.max
import kotlin.math.min
import kotlin.toString

/** Draw the detected pose in preview. */
class PoseGraphic
internal constructor(
    overlay: GraphicOverlay,
    private val pose: Pose,
    private val showInFrameLikelihood: Boolean,
    private val visualizeZ: Boolean,
    private val rescaleZForVisualization: Boolean,
    private val poseClassification: List<String>,
    private val livePreviewViewmodel: LivePreviewViewmodel
) : Graphic(overlay) {
    private var zMin = Float.MAX_VALUE
    private var zMax = Float.MIN_VALUE
    private val classificationTextPaint: Paint
    private val whitePaint: Paint

    init {
        classificationTextPaint = Paint()
        classificationTextPaint.color = Color.WHITE
        classificationTextPaint.textSize = POSE_CLASSIFICATION_TEXT_SIZE
        classificationTextPaint.setShadowLayer(5.0f, 0f, 0f, Color.BLACK)

        whitePaint = Paint()
        whitePaint.strokeWidth = STROKE_WIDTH
        whitePaint.color = Color.WHITE
        whitePaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
    }

    private val hysteresis = 10.0

    private fun updateRepCount() {
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)

        if (leftShoulder == null || rightShoulder == null) {
            return
        }

        val avgY = (leftShoulder.position.y + rightShoulder.position.y) / 2

        if (livePreviewViewmodel.minShoulderY == Float.MAX_VALUE) {
            livePreviewViewmodel.minShoulderY = avgY
            livePreviewViewmodel.maxShoulderY = avgY
        }

        if (livePreviewViewmodel.currentPhase == CurrentPhase.UP) {
            livePreviewViewmodel.minShoulderY = min(livePreviewViewmodel.minShoulderY, avgY)
        } else {
            livePreviewViewmodel.maxShoulderY = max(livePreviewViewmodel.maxShoulderY, avgY)
        }

        val mid = (livePreviewViewmodel.minShoulderY + livePreviewViewmodel.maxShoulderY) / 2

        if (livePreviewViewmodel.currentPhase != CurrentPhase.DOWN && avgY > mid + hysteresis) {
            livePreviewViewmodel.setLivePreviewPhase(CurrentPhase.DOWN)
            livePreviewViewmodel.maxShoulderY = avgY
        } else if (livePreviewViewmodel.currentPhase == CurrentPhase.DOWN && avgY < mid - hysteresis) {
            livePreviewViewmodel.setLivePreviewPhase(CurrentPhase.UP)
            livePreviewViewmodel.minShoulderY = avgY
        }
    }

    override fun draw(canvas: Canvas) {
        val landmarks = pose.allPoseLandmarks
        if (landmarks.isEmpty()) {
            return
        }

        // Draw pose classification text.
        val classificationX = POSE_CLASSIFICATION_TEXT_SIZE * 0.5f
        for (i in poseClassification.indices) {
            val classificationY =
                canvas.height -
                        (POSE_CLASSIFICATION_TEXT_SIZE * 1.5f * (poseClassification.size - i).toFloat())
            canvas.drawText(
                poseClassification[i],
                classificationX,
                classificationY,
                classificationTextPaint
            )
        }

        // Draw all the points
//    for (landmark in landmarks) {
//      drawPoint(canvas, landmark, whitePaint)
//      if (visualizeZ && rescaleZForVisualization) {
//        zMin = min(zMin, landmark.position3D.z)
//        zMax = max(zMax, landmark.position3D.z)
//      }
//    }

        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)

        if (leftShoulder != null && rightShoulder != null) {
            drawLine(canvas, leftShoulder, rightShoulder, whitePaint)
        }

        updateRepCount()

        // draw your rep count somewhere
        canvas.drawText("Reps: ${livePreviewViewmodel.currentRep}", 100f, 360f, classificationTextPaint)
    }

    internal fun drawPoint(canvas: Canvas, landmark: PoseLandmark, paint: Paint) {
        val point = landmark.position3D
        updatePaintColorByZValue(
            paint,
            canvas,
            visualizeZ,
            rescaleZForVisualization,
            point.z,
            zMin,
            zMax
        )
        canvas.drawCircle(translateX(point.x), translateY(point.y), DOT_RADIUS, paint)
    }

    internal fun drawLine(
        canvas: Canvas,
        startLandmark: PoseLandmark?,
        endLandmark: PoseLandmark?,
        paint: Paint
    ) {
        val start = startLandmark!!.position3D
        val end = endLandmark!!.position3D

        // Gets average z for the current body line
        val avgZInImagePixel = (start.z + end.z) / 2
        updatePaintColorByZValue(
            paint,
            canvas,
            visualizeZ,
            rescaleZForVisualization,
            avgZInImagePixel,
            zMin,
            zMax
        )

        canvas.drawLine(
            translateX(start.x),
            translateY(start.y),
            translateX(end.x),
            translateY(end.y),
            paint
        )
    }

    companion object {

        private val DOT_RADIUS = 8.0f
        private val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
        private val STROKE_WIDTH = 10.0f
        private val POSE_CLASSIFICATION_TEXT_SIZE = 60.0f
    }
}
