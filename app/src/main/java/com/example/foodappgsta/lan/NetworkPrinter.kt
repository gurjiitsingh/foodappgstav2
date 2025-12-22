package com.it10x.foodappgstav2.lan

import java.net.Socket

class NetworkPrinter {

    fun print(
        ip: String,
        port: Int = 9100,
        data: ByteArray,
        onError: (String) -> Unit = {}
    ) {
        try {
            val socket = Socket(ip, port)
            val output = socket.getOutputStream()

            output.write(data)
            output.flush()

            socket.close()
        } catch (e: Exception) {
            onError(e.message ?: "LAN printing failed")
        }
    }
}
