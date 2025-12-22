package com.it10x.foodappgstav2.bluetooth

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import java.io.IOException
import java.util.UUID

class BluetoothPrinter {

    private val uuid: UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID

    fun print(context: Context, device: BluetoothDevice, text: String): String {
        return try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)
            socket.connect()

            val output = socket.outputStream
            val init = byteArrayOf(0x1B, 0x40)
            val data = text.toByteArray(Charsets.UTF_8)
            val newLine = byteArrayOf(0x0A)

            output.write(init)
            output.write(data)
            output.write(newLine)
            output.flush()

            socket.close()
            "Printed successfully"

        } catch (e: IOException) {
            "Print error: ${e.message}"
        }
    }
}
