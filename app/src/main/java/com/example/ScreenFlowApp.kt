package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.database.ScreenFlowDatabase
import com.example.data.repository.PresetRepository
import com.example.data.repository.RecordingRepository

class ScreenFlowApp : Application() {

    lateinit var database: ScreenFlowDatabase
        private set

    lateinit var recordingRepository: RecordingRepository
        private set

    lateinit var presetRepository: PresetRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = ScreenFlowDatabase.getDatabase(this)
        recordingRepository = RecordingRepository(database.recordingDao())
        presetRepository = PresetRepository(database.presetDao(), database.brandKitDao())

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val recordingChannel = NotificationChannel(
                CHANNEL_RECORDING_ID,
                "Screen Recording Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows ongoing screen recording controls, timer, and status"
                setShowBadge(false)
            }

            val exportChannel = NotificationChannel(
                CHANNEL_EXPORT_ID,
                "Video Export & Processing",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Shows progress and completion of video saves and exports"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(recordingChannel)
            notificationManager.createNotificationChannel(exportChannel)
        }
    }

    companion object {
        const val CHANNEL_RECORDING_ID = "screenflow_recording_channel"
        const val CHANNEL_EXPORT_ID = "screenflow_export_channel"

        lateinit var instance: ScreenFlowApp
            private set
    }
}
