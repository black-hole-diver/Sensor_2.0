package com.elte.sensor

import android.content.Intent
import android.util.Log
import com.elte.sensor.common.Constants
import com.elte.sensor.common.Constants.INTENT_PHONE_NODE_ID
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

/**
 * The `WearListenerService` is a service that listens for messages from the paired phone
 * and responds to specific message paths to control the `SensorService`.
 *
 * When a message with a recognized path is received, it either starts or stops the `SensorService`
 * depending on the message content.
 */
class WearListenerService : WearableListenerService() {

    /**
     * Called when the service is created. It initializes the message listener.
     */
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Starting wearable message listener...")
    }

    /**
     * Called when a message is received from the paired phone. It processes the message and
     * takes appropriate actions based on the message path.
     *
     * @param messageEvent The received message event.
     */
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.i(TAG, "Message received: ${messageEvent.path} from ${messageEvent.sourceNodeId}")
        when (messageEvent.path) {
            Constants.MESSAGE_PATH_RECORDING_STARTED -> {
                // Start the SensorService when recording is initiated.
                val intent = Intent(this, SensorService::class.java)
                intent.putExtra(INTENT_PHONE_NODE_ID, messageEvent.sourceNodeId)
                startService(intent)
            }
            Constants.MESSAGE_PATH_RECORDING_STOPPED -> {
                // Stop the SensorService when recording is stopped.
                stopService(Intent(this, SensorService::class.java))
            }
            else -> {
                Log.w(TAG, "Unrecognized message path: ${messageEvent.path}")
            }
        }
    }

    companion object {
        // Tag for logging.
        private const val TAG = "ListenerService"
    }
}

