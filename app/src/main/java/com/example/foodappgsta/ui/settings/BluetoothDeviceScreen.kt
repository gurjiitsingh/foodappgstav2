package com.it10x.foodappgstav2.ui.settings

import android.bluetooth.BluetoothDevice
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.it10x.foodappgstav2.data.PrinterRole
import com.it10x.foodappgstav2.printer.bluetooth.RequestBluetoothPermissions
import com.it10x.foodappgstav2.printer.discovery.PrinterDiscoveryRepository
import com.it10x.foodappgstav2.viewmodel.PrinterDiscoveryViewModel
import com.it10x.foodappgstav2.viewmodel.PrinterDiscoveryViewModelFactory
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
@Composable
fun BluetoothDeviceScreen(
    role: PrinterRole     // ✅ REQUIRED (used by navigation)
) {
    val context = LocalContext.current

    val repo = remember {
        PrinterDiscoveryRepository(context)
    }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    val viewModel: PrinterDiscoveryViewModel = viewModel(
        factory = PrinterDiscoveryViewModelFactory(repo)
    )

    // Load paired devices ONCE
    LaunchedEffect(Unit) {
        viewModel.loadPairedBluetoothDevices()
    }

    RequestBluetoothPermissions {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = "Bluetooth Printers - ${role.name}",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            if (viewModel.bluetoothDevices.isEmpty()) {
                Text("No paired Bluetooth devices found")
            } else {
                LazyColumn {
                    items(viewModel.bluetoothDevices) { device ->
                        BluetoothDeviceItem(
                            device = device,
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Selected ${device.name ?: "Printer"} for $role",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // 🔒 Selection only (saving handled elsewhere)
                              //  viewModel.selectDevice(device)
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            TextButton(
                onClick = { backDispatcher?.onBackPressed() }
            ) {
                Text("Back")
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    Toast.makeText(
                        context,
                        "Printer saved for $role",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Text("Save Printer")
            }
            
        }
    }
}

@Composable
private fun BluetoothDeviceItem(
    device: BluetoothDevice,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(device.name ?: "Unknown Device")
            Text(
                device.address,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
