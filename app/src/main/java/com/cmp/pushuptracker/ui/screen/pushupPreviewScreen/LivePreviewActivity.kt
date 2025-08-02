package com.cmp.pushuptracker.ui.screen.pushupPreviewScreen

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.cmp.pushuptracker.R
import com.cmp.pushuptracker.mlKit.posedetector.PoseDetectorProcessor
import com.cmp.pushuptracker.mlKit.prefUtils.PreferenceUtils
import com.cmp.pushuptracker.mlKit.utils.CameraSource
import com.cmp.pushuptracker.mlKit.utils.CameraSourcePreview
import com.cmp.pushuptracker.mlKit.utils.GraphicOverlay
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.ui.GetRepsCountView
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.ui.LivePreviewComposeView
import com.cmp.pushuptracker.ui.screen.pushupPreviewScreen.viewmodel.LivePreviewViewmodel
import com.cmp.pushuptracker.utils.PreferenceUtil
import com.cmp.pushuptracker.utils.PreferenceUtil.TOTAL_INTERVAL
import com.cmp.pushuptracker.utils.PreferenceUtil.TOTAL_REP
import com.cmp.pushuptracker.utils.PreferenceUtil.TOTAL_SET
import com.google.android.gms.common.annotation.KeepName
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException

@AndroidEntryPoint
@KeepName
class LivePreviewActivity : AppCompatActivity() {

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var composeView: ComposeView? = null
    private var repsCountView: ComposeView? = null
    private var selectedModel = POSE_DETECTION

    private val viewModel: LivePreviewViewmodel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_vision_live_preview)

        preview = findViewById(R.id.preview_view)
        if (preview == null) {
            Log.d(TAG, "Preview is null")
        }

        graphicOverlay = findViewById(R.id.graphic_overlay)
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null")
        }

        composeView = findViewById(R.id.compose_view)
        composeView?.setContent {
            LivePreviewComposeView(viewModel)
        }

        repsCountView = findViewById(R.id.reps_count_view)
        repsCountView?.setContent {
            GetRepsCountView(viewModel)
        }

        setupValueFromPreference()

        // Creating adapter for spinner
        createCameraSource(selectedModel)
    }

    private fun createCameraSource(model: String) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = CameraSource(this, graphicOverlay)
        }
        try {
            when (model) {
                POSE_DETECTION -> {
                    val poseDetectorOptions =
                        PreferenceUtils.getPoseDetectorOptionsForLivePreview(this)
                    Log.i(TAG, "Using Pose Detector with options $poseDetectorOptions")
                    val shouldShowInFrameLikelihood = true
                    val visualizeZ = true
                    val rescaleZ = true
                    val runClassification = true
                    cameraSource!!.setMachineLearningFrameProcessor(
                        PoseDetectorProcessor(
                            this,
                            poseDetectorOptions,
                            shouldShowInFrameLikelihood,
                            visualizeZ,
                            rescaleZ,
                            runClassification,
                            /* isStreamMode = */ true,
                            viewModel
                        )
                    )
                }

                else -> Log.e(TAG, "Unknown model: $model")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Can not create image processor: $model", e)
            Toast.makeText(
                applicationContext,
                "Can not create image processor: " + e.message,
                Toast.LENGTH_LONG
            )
                .show()
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private fun startCameraSource() {
        if (cameraSource != null) {
            cameraSource!!.setFacing(CameraSource.CAMERA_FACING_FRONT)
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null")
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null")
                }
                preview!!.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e(TAG, "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
    }

    public override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        createCameraSource(selectedModel)
        startCameraSource()
    }

    /** Stops the camera. */
    override fun onPause() {
        super.onPause()
        preview?.stop()
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (cameraSource != null) {
            cameraSource?.release()
        }
    }

    private fun setupValueFromPreference() {
        val sets = PreferenceUtil.getPreviewPushupPref(
            TOTAL_SET,
            this
        )
        val reps = PreferenceUtil.getPreviewPushupPref(
            TOTAL_REP,
            this
        )
        val interval = PreferenceUtil.getPreviewPushupPref(
            TOTAL_INTERVAL,
            this
        )
        viewModel.setupPushupDataValues(
            sets,
            reps,
            interval
        )
    }

    companion object {
        private const val POSE_DETECTION = "Pose Detection"

        private const val TAG = "LivePreviewActivity"
    }
}
