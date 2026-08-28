package com.example.service

import com.example.data.model.RecordingEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class RecordingStatus {
    object Idle : RecordingStatus()
    data class Countdown(val secondsRemaining: Int) : RecordingStatus()
    data class Recording(
        val durationMs: Long,
        val audioLevelDb: Float, // 0.0 to 1.0
        val isPaused: Boolean = false,
        val filePath: String = "",
        val resolution: String = "1080p",
        val fps: Int = 60
    ) : RecordingStatus()
    data class Completed(
        val recordingEntity: RecordingEntity
    ) : RecordingStatus()
    data class Error(val message: String) : RecordingStatus()
}

object RecordingStateHolder {
    private val _recordingStatus = MutableStateFlow<RecordingStatus>(RecordingStatus.Idle)
    val recordingStatus: StateFlow<RecordingStatus> = _recordingStatus.asStateFlow()

    private val _liveAudioAmplitude = MutableStateFlow(0f)
    val liveAudioAmplitude: StateFlow<Float> = _liveAudioAmplitude.asStateFlow()

    // Event bus for taking in-recording screenshots or toggling tools
    private val _screenshotTrigger = MutableStateFlow<Long?>(null)
    val screenshotTrigger: StateFlow<Long?> = _screenshotTrigger.asStateFlow()

    fun updateStatus(status: RecordingStatus) {
        _recordingStatus.value = status
    }

    fun updateAudioAmplitude(amplitude: Float) {
        _liveAudioAmplitude.value = amplitude
    }

    fun triggerScreenshot() {
        _screenshotTrigger.value = System.currentTimeMillis()
    }

    fun clearStatus() {
        _recordingStatus.value = RecordingStatus.Idle
    }
}
