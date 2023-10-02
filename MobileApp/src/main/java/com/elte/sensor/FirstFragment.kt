package com.elte.sensor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.elte.sensor.common.Constants.MESSAGE_PATH_RECORDING_STARTED
import com.elte.sensor.common.Constants.MESSAGE_PATH_RECORDING_STOPPED
import com.elte.sensor.databinding.FragmentFirstBinding
import com.google.android.gms.wearable.CapabilityClient
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * A Fragment that represents the first screen of the application. This fragment is responsible
 * for interacting with a wearable device, starting and stopping recording, and displaying the
 * connection status.
 *
 * @constructor Creates a new instance of FirstFragment.
 */
class FirstFragment : Fragment() {
    /* Binding for the layout of this fragment. */
    private lateinit var binding: FragmentFirstBinding
    /* List of connected nodes (wearable devices). */
    private var connectedNodes: List<Node> = emptyList()

    /**
     * Called to create the view hierarchy associated with this fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The root view of the fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment and initialize the binding.
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        // Set click listeners for UI buttons.
        binding.refreshConnectedNodes.setOnClickListener {
            findAllWearDevices()
        }
        binding.startRecordingBtn.setOnClickListener {
            startRecording()
        }
        binding.stopRecordingBtn.visibility = View.INVISIBLE
        binding.stopRecordingBtn.setOnClickListener {
            stopRecording()
        }
        return binding.root
    }
    /**
     * Called immediately after onCreateView() has returned and the view hierarchy has been created.
     *
     * @param view The view created by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findAllWearDevices()
    }
    /**
     * Stop the recording process and send a message to connected nodes.
     */
    private fun stopRecording() {
        try {
            sendMessageToConnectedNodes(MESSAGE_PATH_RECORDING_STOPPED)
            binding.connectionStatus.text =
                "Recording stopped. File successfully saved in Downloads folder."
        } catch (throwable: Throwable) {
            Log.e(TAG, throwable.toString())
            binding.connectionStatus.text = throwable.message +
                    "Make sure the watch is connected to the phone through the Galaxy Watch app."
        } finally {
            binding.startRecordingBtn.visibility = View.VISIBLE
            binding.stopRecordingBtn.visibility = View.INVISIBLE
        }
    }
    /**
     * Start the recording process and send a message to connected nodes.
     */
    private fun startRecording() {
        try {
            sendMessageToConnectedNodes(MESSAGE_PATH_RECORDING_STARTED)

            binding.startRecordingBtn.visibility = View.INVISIBLE
            binding.stopRecordingBtn.visibility = View.VISIBLE
            binding.connectionStatus.text = "Recording in process..."
        } catch (throwable: Throwable) {
            Log.e(TAG, throwable.toString())
            binding.connectionStatus.text = throwable.message +
                    "Make sure the watch is connected to the phone through the Galaxy Watch app."
        }
    }
    /**
     * Find all connected wearable devices and update UI accordingly.
     */
    private fun findAllWearDevices() {
        lifecycleScope.launch {
            try {
                val nodeClient = Wearable.getNodeClient(activity)
                binding.phoneNodeId.text =
                    getString(R.string.phone_node_id, nodeClient.localNode.await().id)

                Log.i(TAG, "Looking for nodes...")
                val capInfo = Wearable.getCapabilityClient(context).getCapability(
                    "AGL_MOZGASMERES", CapabilityClient.FILTER_REACHABLE
                ).await()
                connectedNodes = ArrayList(capInfo.nodes)
                Log.i(TAG, "Found ${connectedNodes.size} device(s).")
                if (connectedNodes.isNotEmpty()) {
                    val nodeList =
                        connectedNodes.joinToString(separator = "\n") { "${it.displayName} (${it.id})" }
                    binding.startRecordingBtn.isEnabled = true
                    binding.connectionStatus.text = "Watch connected:\n$nodeList"
                } else {
                    binding.startRecordingBtn.isEnabled = false
                    binding.connectionStatus.text = "Cannot find connected device" +
                    "Make sure the watch is connected to the phone through the Galaxy Watch app."
                }
            } catch (throwable: Throwable) {
                Log.e(TAG, throwable.toString())
                binding.startRecordingBtn.isEnabled = false
                binding.connectionStatus.text =
                    getString(R.string.connection_status, "Error happen in device searching process.")
            }
        }
    }
    /**
     * Send a message to all connected nodes.
     *
     * @param message The message to be sent.
     */
    private fun sendMessageToConnectedNodes(message: String) {
        connectedNodes.forEach { sendMessage(message, it.id) }
    }
    /**
     * Send a message to a specific node.
     *
     * @param message The message to be sent.
     * @param watchNodeId The ID of the target node.
     */
    private fun sendMessage(message: String, watchNodeId: String) {
        lifecycleScope.launch {
            Log.i(TAG, "Sending message: $message to $watchNodeId")
            val messageId = Wearable.getMessageClient(activity)
                .sendMessage(watchNodeId, message, byteArrayOf()).await()
            Log.i(TAG, "messageResult $messageId")
        }
    }

    companion object {
        // TAG for logging.
        private const val TAG = "FirstFragment"
    }
}
