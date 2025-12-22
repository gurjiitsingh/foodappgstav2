package com.it10x.foodappgstav2.printer

import android.content.Context
import com.it10x.foodappgstav2.data.PrinterConfig
import com.it10x.foodappgstav2.data.PrinterType
import com.it10x.foodappgstav2.printer.bluetooth.BluetoothPrinter
import com.it10x.foodappgstav2.printer.lan.LanPrinter
import com.it10x.foodappgstav2.printer.usb.USBPrinter

class PrinterManager(
    private val context: Context
) {

    /**
     * Core printer test (already correct)
     */
    fun printTest(
        config: PrinterConfig,
        onResult: (Boolean) -> Unit
    ) {
        val roleLabel = config.role.name
        when (config.type) {

            PrinterType.BLUETOOTH -> {
                if (config.bluetoothAddress.isBlank()) {
                    onResult(false)
                    return
                }

                BluetoothPrinter.printTest(
                    config.bluetoothAddress,
                    roleLabel,
                    onResult
                )
            }

            PrinterType.LAN -> {
                if (config.ip.isBlank()) {
                    onResult(false)
                    return
                }

                LanPrinter.printTest(
                    config.ip,
                    config.port,
                    roleLabel,
                    onResult
                )
            }

            PrinterType.USB -> {
                val device = config.usbDevice ?: run {
                    onResult(false)
                    return
                }

                USBPrinter.printTest(
                    context,
                    device,
                    onResult
                )
            }

            PrinterType.WIFI -> {
                // future support
                onResult(false)
            }
        }
    }

    /**
     * OPTIONAL helper for role-based usage
     * (ViewModel will call this)
     */
    fun printTestForRole(
        configProvider: () -> PrinterConfig?,
        onResult: (Boolean) -> Unit
    ) {
        val config = configProvider()

        if (config == null) {
            onResult(false)
            return
        }

        printTest(config, onResult)
    }
}
