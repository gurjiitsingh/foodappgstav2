package com.it10x.foodappgstav2.printer.discovery

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager

class PrinterDiscoveryRepository(
    context: Context
) {
    private val usbManager =
        context.getSystemService(Context.USB_SERVICE) as UsbManager

    fun getUsbDevices(): List<UsbDevice> =
        usbManager.deviceList.values.toList()

    fun getPairedBluetoothDevices(): List<BluetoothDevice> {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        return adapter?.bondedDevices?.toList() ?: emptyList()
    }
}
