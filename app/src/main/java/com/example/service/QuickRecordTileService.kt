package com.example.service

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.example.MainActivity

@RequiresApi(Build.VERSION_CODES.N)
class QuickRecordTileService : TileService() {

    companion object {
        const val EXTRA_ACTION_QUICK_RECORD = "EXTRA_ACTION_QUICK_RECORD"
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("EXTRA_ACTION_QUICK_RECORD", true)
        }
        startActivityAndCollapse(intent)
    }

    private fun updateTileState() {
        val tile = qsTile ?: return
        val status = RecordingStateHolder.recordingStatus.value
        if (status is RecordingStatus.Recording) {
            tile.state = Tile.STATE_ACTIVE
            tile.label = "Recording..."
        } else {
            tile.state = Tile.STATE_INACTIVE
            tile.label = "ScreenFlow Rec"
        }
        tile.updateTile()
    }
}
