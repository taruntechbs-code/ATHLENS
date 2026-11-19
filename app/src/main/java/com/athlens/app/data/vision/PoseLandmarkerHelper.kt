package com.athlens.app.data.vision

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarkerResult

class PoseLandmarkerHelper(
    private val context: Context,
    private val listener: LandmarkerListener
) {
    private var poseLandmarker: PoseLandmarker? = null
    private var lastTimestampMs: Long = 0L

    interface LandmarkerListener {
        fun onResults(resultBundle: ResultBundle)
        fun onError(error: String)
    }

    data class ResultBundle(
        val results: PoseLandmarkerResult?,
        val inputImageWidth: Int,
        val inputImageHeight: Int,
        val inferenceTimeMs: Long
    )

    init {
        setupPoseLandmarker()
    }

    private fun setupPoseLandmarker() {
        try {
            val baseOptions = BaseOptions.builder()
                .setModelAssetPath(MODEL_ASSET_PATH)
                .setDelegate(Delegate.CPU)
                .build()

            val options = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(baseOptions)
                .setMinPoseDetectionConfidence(MIN_DETECTION_CONFIDENCE)
                .setMinTrackingConfidence(MIN_TRACKING_CONFIDENCE)
                .setMinPosePresenceConfidence(MIN_PRESENCE_CONFIDENCE)
                .setNumPoses(NUM_POSES)
                .setOutputSegmentationMasks(false)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result: PoseLandmarkerResult, inputImage: MPImage ->
                    val inferenceTime = SystemClock.uptimeMillis() - lastTimestampMs
                    listener.onResults(
                        ResultBundle(
                            results = result,
                            inputImageWidth = inputImage.width,
                            inputImageHeight = inputImage.height,
                            inferenceTimeMs = inferenceTime.coerceAtLeast(1L)
                        )
                    )
                }
                .setErrorListener { error: RuntimeException ->
                    Log.e(TAG, "MediaPipe PoseLandmarker error: ${error.message}", error)
                    listener.onError(error.message ?: "Unknown pose landmarker error")
                }
                .build()

            poseLandmarker = PoseLandmarker.createFromOptions(context, options)
            Log.d(TAG, "MediaPipe PoseLandmarker initialized successfully with CPU delegate")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize PoseLandmarker: ${e.message}", e)
            listener.onError("Failed to initialize vision model: ${e.message}")
        }
    }

    fun detectLiveStream(imageProxy: ImageProxy, isFrontCamera: Boolean) {
        val landmarker = poseLandmarker
        if (landmarker == null) {
            imageProxy.close()
            return
        }

        try {
            val bitmap = Bitmap.createBitmap(
                imageProxy.width,
                imageProxy.height,
                Bitmap.Config.ARGB_8888
            )
            val buffer = imageProxy.planes[0].buffer
            buffer.rewind()
            bitmap.copyPixelsFromBuffer(buffer)

            val rotationDegrees = imageProxy.imageInfo.rotationDegrees.toFloat()
            val matrix = Matrix().apply {
                postRotate(rotationDegrees)
                if (isFrontCamera) {
                    postScale(-1f, 1f, imageProxy.width / 2f, imageProxy.height / 2f)
                }
            }

            val rotatedBitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.width,
                bitmap.height,
                matrix,
                true
            )

            val mpImage = BitmapImageBuilder(rotatedBitmap).build()

            val now = SystemClock.uptimeMillis()
            val frameTime = if (now <= lastTimestampMs) lastTimestampMs + 1 else now
            lastTimestampMs = frameTime

            landmarker.detectAsync(mpImage, frameTime)
        } catch (e: Exception) {
            Log.e(TAG, "Error in detectLiveStream: ${e.message}", e)
            listener.onError("Frame processing error: ${e.message}")
        } finally {
            imageProxy.close()
        }
    }

    fun clear() {
        try {
            poseLandmarker?.close()
            poseLandmarker = null
        } catch (e: Exception) {
            Log.e(TAG, "Error closing PoseLandmarker: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "PoseLandmarkerHelper"
        const val MODEL_ASSET_PATH = "pose_landmarker_lite.task"
        private const val MIN_DETECTION_CONFIDENCE = 0.5f
        private const val MIN_TRACKING_CONFIDENCE = 0.5f
        private const val MIN_PRESENCE_CONFIDENCE = 0.5f
        private const val NUM_POSES = 1
    }
}
