package com.it10x.foodappgstav2.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.*
import com.it10x.foodappgstav2.com.ui.settings.PrinterRoleSelectionScreen
import com.it10x.foodappgstav2.data.PrinterPreferences
import com.it10x.foodappgstav2.data.PrinterRole
import com.it10x.foodappgstav2.printer.PrinterManager
import com.it10x.foodappgstav2.ui.orders.OrdersScreen
import com.it10x.foodappgstav2.ui.settings.*
import com.it10x.foodappgstav2.viewmodel.*

@Composable
fun NavigationHost(
    navController: NavHostController,
    printerManager: PrinterManager,
    printerPreferences: PrinterPreferences,
    realtimeOrdersViewModel: RealtimeOrdersViewModel,
    paddingValues: PaddingValues = PaddingValues(),
    onSavePrinterSettings: () -> Unit = {}   // ✅ ADD THIS
) {

    val printerSettingsViewModel: PrinterSettingsViewModel = viewModel(
        factory = PrinterSettingsViewModelFactory(
            prefs = printerPreferences,
            printerManager = printerManager
        )
    )

    NavHost(
        navController = navController,
        startDestination = "new_order",
        modifier = Modifier.padding(paddingValues)
    ) {


        composable("new_order") {
            Text("New Order Screen (empty for now)")
        }

        composable("orders") {

            // ✅ OrdersScreen DOES NOT create RealtimeOrdersViewModel
            // It RECEIVES the global one from MainActivity
          //  OrdersScreen()
        }

        composable("products") {
            Text("Products Screen (empty for now)")
        }

        composable("categories") {
            Text("Categories Screen (empty for now)")
        }

        // -----------------------------
        // PRINTER SETTINGS
        // -----------------------------

        composable("printer_role_selection") {
            PrinterRoleSelectionScreen(
                prefs = printerPreferences,
                onBillingClick = {
                    navController.navigate("printer_settings/BILLING")
                },
                onKitchenClick = {
                    navController.navigate("printer_settings/KITCHEN")
                }
            )
        }

        composable(
            "printer_settings/{role}",
            arguments = listOf(navArgument("role") { type = NavType.StringType })
        ) { backStackEntry ->

            val role = PrinterRole.valueOf(
                backStackEntry.arguments!!.getString("role")!!
            )

            PrinterSettingsScreen(
                viewModel = printerSettingsViewModel,
                prefs = printerPreferences,
                role = role,

                onSave = {
                    onSavePrinterSettings()
                    navController.popBackStack()
                },

                onBack = {
                    navController.popBackStack()
                },

                onBluetoothSelected = {
                    navController.navigate("bluetooth_devices/${role.name}")
                },

                onUSBSelected = {
                    navController.navigate("usb_devices/${role.name}")
                },

                onLanSelected = {
                    navController.navigate("lan_printer_settings/${role.name}")
                }
            )

        }

// ================= LAN PRINTER =================
        composable("lan_printer_settings/{role}") { backStackEntry ->
            val role = PrinterRole.valueOf(
                backStackEntry.arguments!!.getString("role")!!
            )

            LanPrinterSettingsScreen(
                viewModel = printerSettingsViewModel,
                role = role,
                onBack = { navController.popBackStack() }
            )
        }
        composable("bluetooth_devices/{role}") {
            BluetoothDeviceScreen(
                role = PrinterRole.valueOf(it.arguments!!.getString("role")!!)
            )
        }

        composable("usb_devices/{role}") {
            USBPrinterScreen(
                role = PrinterRole.valueOf(it.arguments!!.getString("role")!!)
            )
        }
    }
}
