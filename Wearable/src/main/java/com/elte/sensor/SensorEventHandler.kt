package com.elte.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import java.io.File
/**
 * The `SensorEventHandler` class is responsible for handling sensor events in the Android Wear application.
 * It implements the [SensorEventListener] interface to listen for changes in sensor data and stores sensor readings.
 *
 * Sensor data can be recorded and exported to a CSV file using the [getFileURI] method.
 */
class SensorEventHandler : SensorEventListener {
    // A list to store sensor readings.
    private val readings = ArrayList<SensorEvent>()
    /**
     * Called when sensor data changes. It adds the new event to the list of readings.
     *
     * @param event The sensor event containing the data.
     */
    override fun onSensorChanged(event: SensorEvent) {
        readings.plusAssign(event)
    }
    /**
     * Called when the accuracy of a sensor changes. It logs the change.
     *
     * @param sensor The sensor whose accuracy changed.
     * @param accuracy The new accuracy value.
     */
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        Log.d(TAG, "Accuracy changed for: $sensor - $accuracy")
    }
    /**
     * Clears all stored sensor readings.
     */
    fun clearReadings() {
        Log.d(TAG, "Clearing recordings array.")
        readings.clear()
    }
    /**
     * Generates a CSV file containing recorded sensor data and returns its [Uri].
     *
     * @param context The application's [Context] for file operations.
     * @return The [Uri] of the generated CSV file.
     */
    fun getFileURI(context: Context): Uri {
        Log.i(TAG, "Writing ${readings.size} records to file.")
        val temp = File.createTempFile("recording", "csv", context.cacheDir)

        // Create a CSV header with all possible sensor names
        val allSensorNames = readings.map { it.sensor.name }.distinct().joinToString(",")
        val csvHeader = "timestamp,$allSensorNames,accuracy"

        // Write the CSV header
        temp.writeText("$csvHeader\n")

        // Write sensor data rows
        for (event in readings) {
            val timestampNanos = System.currentTimeMillis()
            val values = event.values.joinToString(",") { value ->
                if (value.isNaN()) "0.000000" else value.toString() // Use an empty string for NaN values
            }
            val csvRow = "$timestampNanos,$values,${event.accuracy}"
            temp.appendText("$csvRow\n")
        }

        return temp.toUri()
    }

    companion object {
        // Singleton instance of SensorEventHandler.
        val instance = SensorEventHandler()
        // Tag for logging.
        private const val TAG = "SensorEventHandler"
    }
}