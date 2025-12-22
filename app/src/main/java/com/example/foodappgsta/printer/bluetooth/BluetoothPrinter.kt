package com.it10x.foodappgstav2.printer.bluetooth

import android.bluetooth.BluetoothAdapter
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.OutputStream
import java.util.UUID

object BluetoothPrinter {

    private val TAG = "PRINT_BT"

    private val SPP_UUID: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val mainHandler = Handler(Looper.getMainLooper())

    fun printTest(
        mac: String,
        roleLabel: String,
        onResult: (Boolean) -> Unit
    ) {
        Thread {
            try {
                val adapter = BluetoothAdapter.getDefaultAdapter()
                    ?: throw IllegalStateException("Bluetooth not supported")

                if (!adapter.isEnabled) {
                    throw IllegalStateException("Bluetooth is OFF")
                }

                val device = adapter.getRemoteDevice(mac)

                val socket =
                    device.createRfcommSocketToServiceRecord(SPP_UUID)

                adapter.cancelDiscovery()
                socket.connect()

                val output: OutputStream = socket.outputStream

                val data = """
                    ****************************
                         TEST PRINT
                    ****************************
                    Printer Role : $roleLabel
                    Connection   : Bluetooth
                    Status       : OK
                    ----------------------------
                    
                    
                """.trimIndent()

                output.write(data.toByteArray(Charsets.UTF_8))
                output.flush()
                socket.close()

                mainHandler.post {
                    onResult(true)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Bluetooth test print failed", e)
                mainHandler.post {
                    onResult(false)
                }
            }
        }.start()
    }

    fun printText(
        mac: String,
        text: String,
        onResult: (Boolean) -> Unit
    ) {
        Thread {
            try {
                val adapter = BluetoothAdapter.getDefaultAdapter()
                    ?: throw IllegalStateException("Bluetooth not supported")

                if (!adapter.isEnabled) {
                    throw IllegalStateException("Bluetooth is OFF")
                }

                val device = adapter.getRemoteDevice(mac)
                val socket =
                    device.createRfcommSocketToServiceRecord(SPP_UUID)

                adapter.cancelDiscovery()
                socket.connect()

                val output: OutputStream = socket.outputStream
                output.write(text.toByteArray(Charsets.UTF_8))
                output.flush()
                socket.close()

                mainHandler.post {
                    onResult(true)
                }

            } catch (e: Exception) {
                Log.e(TAG, "Bluetooth print failed", e)
                mainHandler.post {
                    onResult(false)
                }
            }
        }.start()
    }
}
