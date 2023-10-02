package com.elte.sensor

import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import com.elte.sensor.common.Constants
import com.google.android.gms.wearable.Wearable
/**
 * The `SensorService` class is a background Android service responsible for handling sensor data
 * and sending it to a paired phone. It starts sensor recording, stops recording on service
 * destruction, and sends the recorded data to the phone.
 */
class SensorService : Service() {

    // Singleton instance of SensorEventHandler for handling sensor events.
    private val sensorEventHandler = SensorEventHandler.instance
    // The unique identifier for the paired phone's node.
    private var phoneNodeId: String? = null
    /**
     * Called when the service is started. It initializes the service and starts sensor recording.
     *
     * @param intent The intent used to start the service.
     * @param flags Additional data about the start request.
     * @param startId A unique integer representing the start request.
     * @return An integer representing the service's behavior.
     */
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "Starting...")
        phoneNodeId = intent.getStringExtra(Constants.INTENT_PHONE_NODE_ID)
        return if (phoneNodeId == null) {
            Log.e(TAG, "Service was started without phone node ID! Stopping...")
            stopSelf()
            START_NOT_STICKY
        } else {
            startRecording()
            START_STICKY
        }
    }
    /**
     * Called when the service is destroyed. It stops sensor recording, unregisters listeners,
     * and sends the recorded data to the paired phone.
     */
    override fun onDestroy() {
        Log.d(TAG, "Stopping...")
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.unregisterListener(sensorEventHandler)

        Log.i(TAG, "Opening channel.")
        val channelClient = Wearable.getChannelClient(this@SensorService)
        channelClient.openChannel(
            phoneNodeId!!,
            Constants.CHANNEL_PATH_SENSOR_READING
        ).continueWith {
            channelClient.sendFile(it.result, sensorEventHandler.getFileURI(this@SensorService))
            Log.i(TAG, "Successfully sent recorded data.")
        }
    }
    /**
     * Called when the service is bound to another component. It returns null as this service
     * does not support binding.
     *
     * @param intent The intent that was used to bind to this service.
     * @return An [IBinder] instance, or null if binding is not supported.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    /**
     * Starts sensor recording by registering listeners for required sensor types.
     * It also clears any existing sensor readings.
     */
    private fun startRecording() {
        sensorEventHandler.clearReadings()
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        // Get a list of all available sensors and required sensor types.
        val availableSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
            .map { i -> i.type }
            .toSet()
        // Find missing required sensors.
        val missingSensors = REQUIRED_SENSORS.keys.subtract(availableSensors)
            .map { i -> REQUIRED_SENSORS[i] }
            .toString()
        Log.d(TAG, "Missing sensors: $missingSensors")

        // Register listeners for required sensors that are available.
        REQUIRED_SENSORS.keys.intersect(availableSensors).forEach { sensorType ->
            val sensor: Sensor = sensorManager.getDefaultSensor(sensorType)
            sensorManager.registerListener(sensorEventHandler, sensor, SAMPLE_RATE)
        }
    }

    companion object {
        // Tag for logging.
        private const val TAG = "SensorService"
        // Sensor sampling rate (50Hz).
        private const val SAMPLE_RATE = 20000
        // Required sensor types and their corresponding names.
        private val REQUIRED_SENSORS = mapOf(
            Sensor.TYPE_ACCELEROMETER to Sensor.STRING_TYPE_ACCELEROMETER,
            Sensor.TYPE_ACCELEROMETER_UNCALIBRATED to Sensor.STRING_TYPE_ACCELEROMETER_UNCALIBRATED,
            Sensor.TYPE_GRAVITY to Sensor.STRING_TYPE_GRAVITY,
            Sensor.TYPE_GYROSCOPE to Sensor.STRING_TYPE_GYROSCOPE,
            Sensor.TYPE_GYROSCOPE_UNCALIBRATED to Sensor.STRING_TYPE_GYROSCOPE_UNCALIBRATED,
            Sensor.TYPE_LINEAR_ACCELERATION to Sensor.STRING_TYPE_LINEAR_ACCELERATION,
            Sensor.TYPE_ROTATION_VECTOR to Sensor.STRING_TYPE_ROTATION_VECTOR
        )
    }

}