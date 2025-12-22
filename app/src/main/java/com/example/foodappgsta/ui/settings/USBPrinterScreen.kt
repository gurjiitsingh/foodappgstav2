package com.it10x.foodappgstav2.ui.settings

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.it10x.foodappgstav2.data.PrinterRole
import com.it10x.foodappgstav2.printer.discovery.PrinterDiscoveryRepository
import com.it10x.foodappgstav2.viewmodel.PrinterDiscoveryViewModel
import com.it10x.foodappgstav2.viewmodel.PrinterDiscoveryViewModelFactory

@Composable
fun USBPrinterScreen(role: PrinterRole) {
    val context = LocalContext.current

    val repo = remember {
        PrinterDiscoveryRepository(context)
    }

    val viewModel: PrinterDiscoveryViewModel = viewModel(
        factory = PrinterDiscoveryViewModelFactory(repo)
    )

    val devices = viewModel.usbDevices

    Column {
        Text("USB Printers - $role")
        devices.forEach {
            Text(it.deviceName)
        }
    }
}
