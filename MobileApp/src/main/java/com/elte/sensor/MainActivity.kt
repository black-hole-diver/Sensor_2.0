package com.elte.sensor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.elte.sensor.databinding.ActivityMainBinding

/**
 * The main activity of the application. This activity serves as the entry point for the app
 * and hosts the primary user interface elements.
 *
 * @constructor Creates a new instance of the MainActivity.
 */
class MainActivity : AppCompatActivity() {
    // Binding for the layout of this activity.
    private lateinit var binding: ActivityMainBinding
    /**
     * Called when the activity is first created. This method sets up the user interface and
     * initializes the main content view.
     *
     * @param savedInstanceState If non-null, this activity is being re-constructed from a previous saved state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
    }

}