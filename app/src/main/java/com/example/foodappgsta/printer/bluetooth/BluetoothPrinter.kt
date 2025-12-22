package com.it10x.foodappgstav2.printer.bluetooth

import android.bluetooth.BluetoothAdapter
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.OutputStream
import java.util.UUID

object BluetoothPrinter {

    private const val TAG = "PRINT_BT"

    private val SPP_UUID: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")

    private val mainHandler = Handler(Looper.getMainLooper())

    // =============================
    // TEST PRINT (same as LAN)
    // =============================
    fun printTest(
        mac: String,
        roleLabel: String,
        onResult: (Boolean) -> Unit
    ) {
        printText(
            mac,
            """
            ****************************
                 TEST PRINT
            ****************************
            Printer Role : $roleLabel
            Connection   : BLUETOOTH
            Status       : OK
            ----------------------------
            
            """.trimIndent(),
            onResult
        )
    }

    // =============================
    // CORE PRINT
    // =============================
    fun printText(
        mac: String,
        text: String,
        onResult: (Boolean) -> Unit
    ) {
        Thread {
            var output: OutputStream? = null

            try {
                val adapter = BluetoothAdapter.getDefaultAdapter()
                    ?: throw IllegalStateException("Bluetooth not supported")

                if (!adapter.isEnabled) {
                    throw IllegalStateException("Bluetooth is OFF")
                }

                Log.d(TAG, "Printing to MAC = $mac")

                val device = adapter.getRemoteDevice(mac)

                val socket = try {
                    device.createRfcommSocketToServiceRecord(SPP_UUID)
                } catch (se: SecurityException) {
                    Log.e(TAG, "Missing BLUETOOTH_CONNECT permission", se)
                    throw se
                }

                adapter.cancelDiscovery()
                socket.connect()

                output = socket.outputStream

                // ESC/POS init
                output.write(byteArrayOf(0x1B, 0x40))
                output.write(text.toByteArray(Charsets.UTF_8))
                output.write(byteArrayOf(0x0A, 0x0A, 0x0A))

                output.flush()
                socket.close()

                mainHandler.post { onResult(true) }

            } catch (e: Exception) {
                Log.e(TAG, "Bluetooth print failed", e)
                mainHandler.post { onResult(false) }

            } finally {
                try { output?.close() } catch (_: Exception) {}
            }
        }.start()
    }

}
