package com.elte.sensor

import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.wearable.*
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MobileListenerService : WearableListenerService() {

    override fun onChannelOpened(channel: ChannelClient.Channel) {
        Log.i(TAG, "Channel opened: " + channel.path)
        Log.i(TAG, "Receiving data from wearable...")
        Wearable.getChannelClient(application).receiveFile(channel, this.createFile(), false)
    }

    override fun onInputClosed(channel: ChannelClient.Channel, p1: Int, p2: Int) {
        Wearable.getChannelClient(application).close(channel)
    }

    private fun createFile(): Uri {
        val downloadsDirectory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val dateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd_HH.mm")
        val formatted = dateTime.format(formatter)
        return File("$downloadsDirectory/sensor_data_$formatted.csv").toUri()
    }

    companion object {
        private const val TAG = "MobileListenerService"
    }
}