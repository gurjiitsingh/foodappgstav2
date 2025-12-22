package com.it10x.foodappgstav2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.foundation.layout.padding
import kotlinx.coroutines.launch

import com.it10x.foodappgstav2.data.PrinterPreferences
import com.it10x.foodappgstav2.data.repository.OrdersRepository
import com.it10x.foodappgstav2.printer.PrinterManager
import com.it10x.foodappgstav2.viewmodel.OrdersViewModel
import com.it10x.foodappgstav2.viewmodel.RealtimeOrdersViewModel
import com.it10x.foodappgstav2.navigation.NavigationHost
import com.it10x.foodappgstav2.printer.AutoPrintManager

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // ------------------------------------
// CORE SINGLETON OBJECTS (ONCE)
// ------------------------------------
            val printerPreferences = remember { PrinterPreferences(this) }
            val printerManager = remember { PrinterManager(this) }
            val ordersRepository = remember { OrdersRepository() }
            val ordersViewModel = remember { OrdersViewModel(printerManager) }


            val autoPrintManager = remember {
                AutoPrintManager(
                    ordersViewModel = ordersViewModel,
                    ordersRepository = ordersRepository
                )
            }

            // ------------------------------------
            // REALTIME ORDERS VIEWMODEL (FACTORY)
            // ------------------------------------
            val realtimeOrdersVM: RealtimeOrdersViewModel =
                viewModel(factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return RealtimeOrdersViewModel(
                            application = application,
                            autoPrintManager = autoPrintManager
                        ) as T
                    }
                })

            // Start Firestore listener ONCE
            LaunchedEffect(Unit) {
                realtimeOrdersVM.startListening()
            }

            // ------------------------------------
            // UI STATE
            // ------------------------------------
            val navController = rememberNavController()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        Text(
                            "Menu",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(16.dp)
                        )

                        NavigationDrawerItem(
                            label = { Text("New Order") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("new_order")
                            }
                        )

                        NavigationDrawerItem(
                            label = { Text("Orders") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("orders")
                            }
                        )

                        NavigationDrawerItem(
                            label = { Text("Products") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("products")
                            }
                        )

                        NavigationDrawerItem(
                            label = { Text("Categories") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("categories")
                            }
                        )

                        NavigationDrawerItem(
                            label = { Text("Printer Settings") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                               navController.navigate("printer_role_selection")
                            }
                        )
                    }
                }
            ) {
              Scaffold(
    topBar = {
        CenterAlignedTopAppBar(
            title = { Text("POS") },
            navigationIcon = {
                IconButton(
                    onClick = { scope.launch { drawerState.open() } }
                ) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu")
                }
            },
            actions = {
                StopSoundButton(viewModel = realtimeOrdersVM)
            }
        )
    }
) { paddingValues ->

                  NavigationHost(
                      navController = navController,
                      printerManager = printerManager,
                      printerPreferences = printerPreferences,
                      realtimeOrdersViewModel = realtimeOrdersVM,
                      paddingValues = paddingValues,
                      onSavePrinterSettings = {
                          // optional: analytics / toast / sync
                      }
                  )

              }
            }
        }
    }

    @Composable
    fun StopSoundButton(viewModel: RealtimeOrdersViewModel) {
        Button(
            onClick = {
                viewModel.stopRingtone() // ✅ USING EXISTING FUNCTION
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("STOP SOUND")
        }
    }

}
