package com.it10x.foodappgstav2.ui.orders

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color
import com.it10x.foodappgstav2.data.PrinterPreferences
import com.it10x.foodappgstav2.printer.PrinterManager
import com.it10x.foodappgstav2.viewmodel.OrdersViewModel
import com.it10x.foodappgstav2.viewmodel.RealtimeOrdersViewModel
import com.it10x.foodappgstav2.viewmodel.OrdersViewModelFactory

@Composable
fun OrdersScreen(
    printerManager: PrinterManager,
    ordersViewModel: OrdersViewModel,                   // ✅ pass from MainActivity
    realtimeOrdersViewModel: RealtimeOrdersViewModel   // ✅ pass from MainActivity
) {

    // -----------------------------
    // START DATA LOADING (ONCE)
    // -----------------------------
    LaunchedEffect(Unit) {
        ordersViewModel.loadFirstPage()
    }

    // -----------------------------
    // STATE COLLECTION
    // -----------------------------
    val pagedOrders by ordersViewModel.orders.collectAsState()
    val realtimeOrders by realtimeOrdersViewModel.realtimeOrders.collectAsState()
    val loading by ordersViewModel.loading.collectAsState()

    val combinedOrders = remember(realtimeOrders, pagedOrders) {
        val realtimeIds = realtimeOrders.map { it.id }.toSet()
        (realtimeOrders + pagedOrders.filter { it.id !in realtimeIds })
            .sortedByDescending { it.createdAt }
    }

    // -----------------------------
    // UI
    // -----------------------------
    Column(modifier = Modifier.padding(12.dp)) {

        Text("Orders", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))

        when {
            loading && combinedOrders.isEmpty() -> Text("Loading orders...")
            combinedOrders.isEmpty() -> Text("No orders found")
            else -> {
                TableHeader()
                Spacer(Modifier.height(4.dp))

                combinedOrders.forEach { order ->
                    OrderTableRow(
                        order = order,
                        onOrderClick = { println("OPEN ORDER DETAIL FOR: ${it.srno}") },
                        onPrintClick = { ordersViewModel.printOrder(it) } // ✅ works
                    )
                }

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { ordersViewModel.loadPrevPage() },
                        enabled = !loading
                    ) { Text("← Previous") }

                    Button(
                        onClick = { ordersViewModel.loadNextPage() },
                        enabled = !loading
                    ) { Text("Next →") }
                }
            }
        }
    }
}
