/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.elte.sensor

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.elte.sensor.theme.SensorTheme
/**
 * The `MainActivity` class serves as the entry point for the Android Wear application.
 * It extends [ComponentActivity] and handles the creation of the application's user interface.
 */
class MainActivity : ComponentActivity() {
    /**
     * Called when the activity is first created. It initializes the activity's UI
     * and checks for necessary permissions before proceeding.
     *
     * @param savedInstanceState The saved instance state, if any.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.checkPermission()

        setContent {
            WearApp()
        }
    }
    /**
     * Checks if the necessary permission for body sensors is granted.
     * If not, it requests the permission from the user.
     */
    private fun checkPermission() {
        if (checkSelfPermission(Manifest.permission.BODY_SENSORS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Requesting necessary permissions...")
            requestPermissions(arrayOf(Manifest.permission.BODY_SENSORS), 1)
        } else {
            Log.d(TAG, "Permissions already granted.")
        }
    }
    /**
     * A constant used for logging within this class.
     */
    companion object {
        private const val TAG = "MainActivity"
    }
}
/**
 * The `WearApp` composable function represents the main user interface of the Android Wear application.
 * It displays a message to the user while waiting for connections or performing other actions.
 */
@Composable
fun WearApp() {
    SensorTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.primary,
                text = stringResource(R.string.waiting_for_connections)
            )
        }
    }
}
/**
 * The `DefaultPreview` composable function is used for previewing the appearance of the Android Wear application's user interface.
 * It displays a preview of the `WearApp` composable function, showing how the app's UI will look.
 *
 * @see WearApp
 */
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp()
}