package com.it10x.foodappgstav2.viewmodel

import android.hardware.usb.UsbDevice
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.it10x.foodappgstav2.data.PrinterConfig
import com.it10x.foodappgstav2.data.PrinterPreferences
import com.it10x.foodappgstav2.data.PrinterRole
import com.it10x.foodappgstav2.data.PrinterType
import com.it10x.foodappgstav2.printer.PrinterManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PrinterSettingsViewModel(
    private val prefs: PrinterPreferences,
    private val printerManager: PrinterManager
) : ViewModel() {

    // ================= PRINTER TYPE =================
    val printerTypeMap =
        mutableStateMapOf<PrinterRole, MutableStateFlow<PrinterType>>()

    // ================= BLUETOOTH =================
    val btNameMap =
        mutableStateMapOf<PrinterRole, MutableStateFlow<String>>()

    // ================= USB =================
    val selectedUSBDeviceMap =
        mutableStateMapOf<PrinterRole, MutableStateFlow<UsbDevice?>>()

    // ================= LAN =================
    val printerIPMap =
        mutableStateMapOf<PrinterRole, String>()

    val printerPortMap =
        mutableStateMapOf<PrinterRole, Int>()

    init {
        PrinterRole.values().forEach { role ->

            printerTypeMap[role] =
                MutableStateFlow(prefs.getPrinterType(role))

            btNameMap[role] =
                MutableStateFlow(prefs.getBluetoothPrinterName(role))

            selectedUSBDeviceMap[role] =
                MutableStateFlow(null)

            printerIPMap[role] =
                prefs.getLanPrinterIP(role)

            printerPortMap[role] =
                prefs.getLanPrinterPort(role)
        }
    }

    // ================= UPDATE FUNCTIONS =================

  
fun updatePrinterType(role: PrinterRole, type: PrinterType) {
    printerTypeMap[role]?.value = type
    prefs.savePrinterType(role, type)  // ✅ use savePrinterType instead of setPrinterType
}
    fun updateBluetoothPrinter(role: PrinterRole, name: String) {
        btNameMap[role]?.value = name
        prefs.setBluetoothPrinterName(role, name)
    }

    fun updateUSBPrinter(role: PrinterRole, device: UsbDevice?) {
        selectedUSBDeviceMap[role]?.value = device
    }

    fun updateLanIP(role: PrinterRole, ip: String) {
        printerIPMap[role] = ip
        prefs.setLanPrinterIP(role, ip)
    }

    fun updateLanPort(role: PrinterRole, port: Int) {
        printerPortMap[role] = port
        prefs.setLanPrinterPort(role, port)
    }


private fun buildPrinterConfig(role: PrinterRole): PrinterConfig {
    return PrinterConfig(
        role = role, // ✅ REQUIRED
        type = printerTypeMap[role]?.value ?: PrinterType.BLUETOOTH,
        bluetoothAddress = prefs.getBluetoothPrinterAddress(role),
        ip = prefs.getLanPrinterIP(role),
        port = prefs.getLanPrinterPort(role),
        usbDevice = selectedUSBDeviceMap[role]?.value
    )
}

fun testPrint(
    role: PrinterRole,
    onResult: (Boolean) -> Unit
) {
     val config = PrinterConfig(
        type = prefs.getPrinterType(role),
        bluetoothAddress = prefs.getBluetoothPrinterAddress(role),
        ip = prefs.getLanPrinterIP(role),
        port = prefs.getLanPrinterPort(role),
        usbDevice = selectedUSBDeviceMap[role]?.value,
        role = role                        // ✅ HERE
    )

    android.util.Log.d(
        "PRINT_VM",
        "Test print requested. Role=$role, Type=${config.type}"
    )

    printerManager.printTest(
        config = config
    ) { success ->
        android.util.Log.d(
            "PRINT_VM",
            "Print result ($role) = $success"
        )
        onResult(success)
    }
}





}
