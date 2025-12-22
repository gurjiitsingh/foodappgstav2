package com.it10x.foodappgstav2.printer.usb

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.*
import android.util.Log
import com.it10x.foodappgstav2.usb.USBPermissionHelper
import com.it10x.foodappgstav2.usb.UsbPermissionReceiver
import kotlinx.coroutines.*

object USBPrinter {

    private const val TAG = "USBPrinter"
    const val ACTION_USB_PERMISSION = "com.it10x.foodappgstav2.USB_PERMISSION"

    private var usbManager: UsbManager? = null
    private var usbDevice: UsbDevice? = null
    private var connection: UsbDeviceConnection? = null
    private var outEndpoint: UsbEndpoint? = null
    private var permissionReceiver: UsbPermissionReceiver? = null

    /**
     * Initialize USB manager and request permission (Android 14 SAFE)
     */
    fun init(context: Context, device: UsbDevice, onReady: (Boolean) -> Unit) {
        usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        usbDevice = device

        // Request USB permission using helper
        USBPermissionHelper.requestPermission(context, device) {
            try {
                setupConnection(device)
                onReady(true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to setup USB connection: ${e.message}")
                onReady(false)
            }
        }
    }


    /**
     * Setup USB connection and endpoint
     */
    private fun setupConnection(device: UsbDevice) {
        val iface = device.getInterface(0)

        outEndpoint = null
        for (i in 0 until iface.endpointCount) {
            val ep = iface.getEndpoint(i)
            if (
                ep.type == UsbConstants.USB_ENDPOINT_XFER_BULK &&
                ep.direction == UsbConstants.USB_DIR_OUT
            ) {
                outEndpoint = ep
                break
            }
        }

        if (outEndpoint == null) {
            throw IllegalStateException("No OUT endpoint found")
        }

        connection = usbManager?.openDevice(device)
            ?: throw IllegalStateException("Unable to open USB device")

        connection?.claimInterface(iface, true)
        Log.d(TAG, "USB printer connected: ${device.deviceName}")
    }

    /**
     * Test print
     */
    fun printTest(
        context: Context,
        device: UsbDevice,
        onResult: (Boolean) -> Unit
    ) {
        init(context, device) { ready ->
            if (!ready) {
                onResult(false)
                return@init
            }

            printText("USB TEST PRINT\n\n") {
                onResult(it)
            }
        }
    }

    /**
     * Print text
     */
    fun printText(text: String, onResult: (Boolean) -> Unit) {
        val ep = outEndpoint
        val conn = connection

        if (ep == null || conn == null) {
            Log.e(TAG, "USB printer not ready")
            onResult(false)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val init = byteArrayOf(0x1B, 0x40) // ESC @
                val feedCut =
                    byteArrayOf(0x0A, 0x0A, 0x1D, 0x56, 0x41, 0x10)

                val data =
                    init + text.toByteArray(Charsets.US_ASCII) + feedCut

                val sent = conn.bulkTransfer(ep, data, data.size, 5000)

                withContext(Dispatchers.Main) {
                    onResult(sent > 0)
                }

            } catch (e: Exception) {
                Log.e(TAG, "USB print error", e)
                withContext(Dispatchers.Main) {
                    onResult(false)
                }
            }
        }
    }

    /**
     * List connected USB devices
     */
    fun getConnectedUSBDevices(context: Context): List<UsbDevice> {
        val manager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        return manager.deviceList.values.toList()
    }

    /**
     * Release connection
     */
    fun release() {
        try {
            connection?.close()
        } catch (_: Exception) {}
        connection = null
        outEndpoint = null
        usbDevice = null
    }
}
