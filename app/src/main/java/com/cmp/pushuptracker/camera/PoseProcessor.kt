package com.cmp.pushuptracker.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.YuvImage
import android.media.Image
import android.os.SystemClock
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker.PoseLandmarkerOptions
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult
import java.io.ByteArrayOutputStream

object PoseProcessor {
    private var poseLandmarker: PoseLandmarker? = null
    private var lastAnalyzedTime = 0L
    private const val FRAME_INTERVAL_MS = 150L

    fun initPoseLandmarker(context: Context) {
        val baseOptionsBuilder =
            BaseOptions.builder().setModelAssetPath("pose_landmarker_full.task")

        val options = PoseLandmarkerOptions.builder()
            .setBaseOptions(baseOptionsBuilder.build())
            .setRunningMode(RunningMode.VIDEO)
            .build()

        poseLandmarker = PoseLandmarker.createFromOptions(context, options)
    }

    @OptIn(ExperimentalGetImage::class)
    fun analyzeImage(imageProxy: ImageProxy, onPoseResult: (PoseLandmarkerResult?) -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastAnalyzedTime < FRAME_INTERVAL_MS) {
            imageProxy.close()
            return
        }
        lastAnalyzedTime = currentTime

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val bitmap = imageToBitmap(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val mpImage = BitmapImageBuilder(bitmap).build()

        val timestamp = SystemClock.uptimeMillis() * 1000

        try {
            val result = poseLandmarker?.detectForVideo(mpImage, timestamp)
            onPoseResult(result)
        } catch (e: Exception) {
            Log.e("PoseProcessor", "Error during pose detection", e)
        } finally {
            imageProxy.close()
        }
    }

    private fun imageToBitmap(image: Image, rotation: Int): Bitmap {
        val yBuffer = image.planes[0].buffer
        val uBuffer = image.planes[1].buffer
        val vBuffer = image.planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(android.graphics.Rect(0, 0, image.width, image.height), 90, out)

        val jpegBytes = out.toByteArray()
        val originalBitmap = BitmapFactory.decodeByteArray(jpegBytes, 0, jpegBytes.size)

        // Flip horizontally for front camera
        val matrix = Matrix().apply { preScale(-1f, 1f) }
        return Bitmap.createBitmap(
            originalBitmap,
            0,
            0,
            originalBitmap.width,
            originalBitmap.height,
            matrix,
            true
        )
    }
}
