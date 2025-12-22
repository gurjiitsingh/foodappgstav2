package com.it10x.foodappgstav2.data

import android.content.Context

class PrinterPreferences(context: Context) {

    private val prefs = context.getSharedPreferences("printer_prefs", Context.MODE_PRIVATE)

    // -------------------------
    // PRINTER TYPE
    // -------------------------
    fun savePrinterType(role: PrinterRole, type: PrinterType) {
        prefs.edit()
            .putString("printer_${role.name.lowercase()}_type", type.name)
            .apply()
    }

    fun getPrinterType(role: PrinterRole): PrinterType {
        return try {
            PrinterType.valueOf(
                prefs.getString(
                    "printer_${role.name.lowercase()}_type",
                    PrinterType.LAN.name
                )!!
            )
        } catch (e: Exception) {
            PrinterType.LAN
        }
    }

    // -------------------------
    // LAN PRINTER
    // -------------------------
    fun saveLanPrinter(role: PrinterRole, ip: String, port: Int = 9100) {
        prefs.edit()
            .putString("printer_${role.name.lowercase()}_ip", ip)
            .putInt("printer_${role.name.lowercase()}_port", port)
            .apply()
    }

    fun getLanPrinterIP(role: PrinterRole): String =
        prefs.getString("printer_${role.name.lowercase()}_ip", "") ?: ""

    fun getLanPrinterPort(role: PrinterRole): Int =
        prefs.getInt("printer_${role.name.lowercase()}_port", 9100)

    fun setLanPrinterIP(role: PrinterRole, ip: String) {
        prefs.edit().putString("printer_${role.name.lowercase()}_ip", ip).apply()
    }

    fun setLanPrinterPort(role: PrinterRole, port: Int) {
        prefs.edit().putInt("printer_${role.name.lowercase()}_port", port).apply()
    }

    // -------------------------
    // BLUETOOTH PRINTER
    // -------------------------
    fun saveBluetoothPrinter(role: PrinterRole, name: String, address: String) {
        prefs.edit()
            .putString("bt_printer_${role.name.lowercase()}_name", name)
            .putString("bt_printer_${role.name.lowercase()}_address", address)
            .apply()
    }

    fun getBluetoothPrinterName(role: PrinterRole): String =
        prefs.getString("bt_printer_${role.name.lowercase()}_name", "") ?: ""

    fun getBluetoothPrinterAddress(role: PrinterRole): String =
        prefs.getString("bt_printer_${role.name.lowercase()}_address", "") ?: ""

    fun setBluetoothPrinterName(role: PrinterRole, name: String) {
        prefs.edit().putString("bt_printer_${role.name.lowercase()}_name", name).apply()
    }

    fun setBluetoothPrinterAddress(role: PrinterRole, address: String) {
        prefs.edit().putString("bt_printer_${role.name.lowercase()}_address", address).apply()
    }

    // -------------------------
    // USB PRINTER
    // -------------------------
    fun saveUSBPrinter(role: PrinterRole, name: String, deviceId: Int) {
        prefs.edit()
            .putString("usb_printer_${role.name.lowercase()}_name", name)
            .putInt("usb_printer_${role.name.lowercase()}_id", deviceId)
            .apply()
    }

    fun getUSBPrinterName(role: PrinterRole): String =
        prefs.getString("usb_printer_${role.name.lowercase()}_name", "") ?: ""

    fun getUSBPrinterId(role: PrinterRole): Int =
        prefs.getInt("usb_printer_${role.name.lowercase()}_id", -1)

    fun setUSBPrinterName(role: PrinterRole, name: String) {
        prefs.edit().putString("usb_printer_${role.name.lowercase()}_name", name).apply()
    }

    fun setUSBPrinterId(role: PrinterRole, deviceId: Int) {
        prefs.edit().putInt("usb_printer_${role.name.lowercase()}_id", deviceId).apply()
    }

    // -------------------------
    // CLEAR ALL
    // -------------------------
    fun clear() {
        prefs.edit().clear().apply()
    }
}
