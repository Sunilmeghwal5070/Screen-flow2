package com.example.service

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.ScreenFlowApp
import com.example.data.model.RecordingEntity
import com.example.util.DeviceCapabilities
import com.example.util.VideoMetadataHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class ScreenRecordingService : Service() {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaRecorder: MediaRecorder? = null

    private var resultCode: Int = 0
    private var resultData: Intent? = null

    private var recordingFile: File? = null
    private var startTimeMs: Long = 0L
    private var pausedDurationMs: Long = 0L
    private var pauseStartTimeMs: Long = 0L
    private var isPaused: Boolean = false

    private var floatingBubbleManager: FloatingBubbleManager? = null

    private var width: Int = 1080
    private var height: Int = 1920
    private var densityDpi: Int = DisplayMetrics.DENSITY_DEFAULT
    private var fps: Int = 60
    private var bitrateMbps: Int = 12
    private var audioSource: String = "Device + Mic"
    private var resolutionLabel: String = "1080p"

    private val serviceScope = CoroutineScope(Dispatchers.IO + Job())
    private var timerJob: Job? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val projectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            super.onStop()
            mainHandler.post {
                stopRecording()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        floatingBubbleManager = FloatingBubbleManager(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                resultCode = intent.getIntExtra(EXTRA_RESULT_CODE, 0)
                resultData = intent.getParcelableExtra(EXTRA_RESULT_DATA)
                width = intent.getIntExtra(EXTRA_WIDTH, 1080)
                height = intent.getIntExtra(EXTRA_HEIGHT, 1920)
                fps = intent.getIntExtra(EXTRA_FPS, 60)
                bitrateMbps = intent.getIntExtra(EXTRA_BITRATE, 12)
                audioSource = intent.getStringExtra(EXTRA_AUDIO_SOURCE) ?: "Device + Mic"
                resolutionLabel = intent.getStringExtra(EXTRA_RESOLUTION) ?: "1080p"

                startForegroundServiceWithNotification()
                startRecording()
            }
            ACTION_PAUSE -> pauseRecording()
            ACTION_RESUME -> resumeRecording()
            ACTION_STOP -> stopRecording()
            ACTION_SCREENSHOT -> RecordingStateHolder.triggerScreenshot()
        }
        return START_NOT_STICKY
    }

    private fun startForegroundServiceWithNotification() {
        val notification = buildRecordingNotification("Recording in progress...", 0L)
        val hasMic = !audioSource.contains("Mute", ignoreCase = true)
        val hasMicPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (hasMic && hasMicPermission) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION or
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE
                } else {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                }
            } else {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
            }
            startForeground(NOTIFICATION_ID, notification, type)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun buildRecordingNotification(statusText: String, durationMs: Long): Notification {
        val appIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this, 0, appIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, ScreenRecordingService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseResumeIntent = Intent(this, ScreenRecordingService::class.java).apply {
            action = if (isPaused) ACTION_RESUME else ACTION_PAUSE
        }
        val pauseResumePendingIntent = PendingIntent.getService(
            this, 2, pauseResumeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val screenshotIntent = Intent(this, ScreenRecordingService::class.java).apply { action = ACTION_SCREENSHOT }
        val screenshotPendingIntent = PendingIntent.getService(
            this, 3, screenshotIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val durationFormatted = DeviceCapabilities.formatDuration(durationMs)
        val pauseResumeLabel = if (isPaused) "Resume" else "Pause"

        return NotificationCompat.Builder(this, ScreenFlowApp.CHANNEL_RECORDING_ID)
            .setContentTitle("ScreenFlow Studio · $durationFormatted")
            .setContentText(statusText)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(contentPendingIntent)
            .setOngoing(true)
            .addAction(0, pauseResumeLabel, pauseResumePendingIntent)
            .addAction(0, "Stop", stopPendingIntent)
            .addAction(0, "Screenshot", screenshotPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun startRecording() {
        try {
            val projectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            if (resultData == null) {
                RecordingStateHolder.updateStatus(RecordingStatus.Error("Media projection permission not provided"))
                stopSelf()
                return
            }

            mediaProjection = projectionManager.getMediaProjection(resultCode, resultData!!)
            if (mediaProjection == null) {
                RecordingStateHolder.updateStatus(RecordingStatus.Error("Failed to initialize MediaProjection"))
                stopSelf()
                return
            }
            mediaProjection?.registerCallback(projectionCallback, mainHandler)

            // Get display density
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(metrics)
            densityDpi = if (metrics.densityDpi > 0) metrics.densityDpi else DisplayMetrics.DENSITY_DEFAULT

            val validWidth = if (width > 0) (width / 2) * 2 else (metrics.widthPixels / 2) * 2
            val validHeight = if (height > 0) (height / 2) * 2 else (metrics.heightPixels / 2) * 2

            // Setup recording output file
            val moviesDir = File(getExternalFilesDir(null) ?: filesDir, "Recordings").apply { mkdirs() }
            val fileName = VideoMetadataHelper.generateFileName("ScreenFlow", "mp4")
            recordingFile = File(moviesDir, fileName)

            // Setup MediaRecorder
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(this)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            val hasMic = !audioSource.contains("Mute", ignoreCase = true)
            val hasMicPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            var audioConfigured = false

            if (hasMic && hasMicPerm) {
                try {
                    mediaRecorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
                    audioConfigured = true
                } catch (e: Exception) {
                    audioConfigured = false
                }
            }

            mediaRecorder?.apply {
                setVideoSource(MediaRecorder.VideoSource.SURFACE)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                if (audioConfigured) {
                    try {
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setAudioEncodingBitRate(128_000)
                        setAudioSamplingRate(44100)
                    } catch (_: Exception) {}
                }
                setVideoSize(validWidth, validHeight)
                setVideoFrameRate(fps.coerceIn(15, 60))
                setVideoEncodingBitRate((bitrateMbps.coerceIn(2, 50)) * 1_000_000)
                setOutputFile(recordingFile!!.absolutePath)
                prepare()
            }

            val surface = mediaRecorder?.surface ?: throw IllegalStateException("Surface is null")

            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ScreenFlowVirtualDisplay",
                validWidth,
                validHeight,
                densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                surface,
                null,
                null
            )

            mediaRecorder?.start()
            startTimeMs = System.currentTimeMillis()
            isPaused = false
            pausedDurationMs = 0L

            floatingBubbleManager?.show()
            startTimerLoop()

        } catch (e: Exception) {
            e.printStackTrace()
            RecordingStateHolder.updateStatus(RecordingStatus.Error("Recording error: ${e.localizedMessage ?: "Unknown"}"))
            cleanUp()
            stopSelf()
        }
    }

    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = serviceScope.launch {
            while (isActive) {
                val currentDuration = if (isPaused) {
                    (pauseStartTimeMs - startTimeMs) - pausedDurationMs
                } else {
                    (System.currentTimeMillis() - startTimeMs) - pausedDurationMs
                }.coerceAtLeast(0L)

                var amplitude = 0f
                if (!isPaused) {
                    try {
                        val maxAmp = mediaRecorder?.maxAmplitude ?: 0
                        amplitude = (maxAmp / 32767f).coerceIn(0f, 1f)
                    } catch (_: Exception) {}
                }

                RecordingStateHolder.updateAudioAmplitude(amplitude)
                RecordingStateHolder.updateStatus(
                    RecordingStatus.Recording(
                        durationMs = currentDuration,
                        audioLevelDb = amplitude,
                        isPaused = isPaused,
                        filePath = recordingFile?.absolutePath ?: "",
                        resolution = resolutionLabel,
                        fps = fps
                    )
                )

                // Update notification every second
                if (currentDuration % 1000 < 100) {
                    val notification = buildRecordingNotification(
                        if (isPaused) "Paused" else "Recording screen...",
                        currentDuration
                    )
                    val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.notify(NOTIFICATION_ID, notification)
                }

                delay(100)
            }
        }
    }

    private fun pauseRecording() {
        if (!isPaused && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                mediaRecorder?.pause()
                isPaused = true
                pauseStartTimeMs = System.currentTimeMillis()
                floatingBubbleManager?.updateState(true)
                val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(NOTIFICATION_ID, buildRecordingNotification("Recording paused", pauseStartTimeMs - startTimeMs - pausedDurationMs))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun resumeRecording() {
        if (isPaused && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                mediaRecorder?.resume()
                isPaused = false
                pausedDurationMs += (System.currentTimeMillis() - pauseStartTimeMs)
                floatingBubbleManager?.updateState(false)
                val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.notify(NOTIFICATION_ID, buildRecordingNotification("Recording resumed", System.currentTimeMillis() - startTimeMs - pausedDurationMs))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun stopRecording() {
        timerJob?.cancel()
        val totalDuration = if (startTimeMs > 0) {
            (System.currentTimeMillis() - startTimeMs) - pausedDurationMs
        } else 0L

        try {
            mediaRecorder?.stop()
        } catch (_: Exception) {}

        cleanUp()

        // Process saved video file
        serviceScope.launch {
            val file = recordingFile
            if (file != null && file.exists() && file.length() > 0) {
                val thumbnailPath = VideoMetadataHelper.generateThumbnail(applicationContext, file.absolutePath)
                val videoInfo = VideoMetadataHelper.extractVideoInfo(file.absolutePath)

                val entity = RecordingEntity(
                    title = file.nameWithoutExtension,
                    filePath = file.absolutePath,
                    durationMs = if (videoInfo.durationMs > 0) videoInfo.durationMs else totalDuration,
                    fileSizeBytes = file.length(),
                    resolution = resolutionLabel,
                    width = width,
                    height = height,
                    fps = fps,
                    bitrateMbps = bitrateMbps,
                    audioSource = audioSource,
                    thumbnailPath = thumbnailPath
                )

                val savedId = ScreenFlowApp.instance.recordingRepository.insertRecording(entity)
                val completeEntity = entity.copy(id = savedId)

                mainHandler.post {
                    RecordingStateHolder.updateStatus(RecordingStatus.Completed(completeEntity))
                }
            } else {
                mainHandler.post {
                    RecordingStateHolder.updateStatus(RecordingStatus.Idle)
                }
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun cleanUp() {
        try {
            floatingBubbleManager?.hide()
        } catch (_: Exception) {}

        try {
            mediaRecorder?.reset()
            mediaRecorder?.release()
        } catch (_: Exception) {}
        mediaRecorder = null

        try {
            virtualDisplay?.release()
        } catch (_: Exception) {}
        virtualDisplay = null

        try {
            mediaProjection?.unregisterCallback(projectionCallback)
            mediaProjection?.stop()
        } catch (_: Exception) {}
        mediaProjection = null
    }

    override fun onDestroy() {
        cleanUp()
        timerJob?.cancel()
        super.onDestroy()
    }

    companion object {
        const val NOTIFICATION_ID = 9012

        const val ACTION_START = "com.example.service.ACTION_START"
        const val ACTION_PAUSE = "com.example.service.ACTION_PAUSE"
        const val ACTION_RESUME = "com.example.service.ACTION_RESUME"
        const val ACTION_STOP = "com.example.service.ACTION_STOP"
        const val ACTION_SCREENSHOT = "com.example.service.ACTION_SCREENSHOT"

        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_RESULT_DATA = "extra_result_data"
        const val EXTRA_WIDTH = "extra_width"
        const val EXTRA_HEIGHT = "extra_height"
        const val EXTRA_FPS = "extra_fps"
        const val EXTRA_BITRATE = "extra_bitrate"
        const val EXTRA_AUDIO_SOURCE = "extra_audio_source"
        const val EXTRA_RESOLUTION = "extra_resolution"
    }
}
