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
    realtimeOrdersViewModel: RealtimeOrdersViewModel
) {
    val realtimeOrdersViewModel = realtimeOrdersViewModel  // use the passed instance


    // -----------------------------
    // VIEW MODELS
    // -----------------------------

    // OrdersViewModel
    // - Handles pagination
    // - Handles printing
    // This ViewModel is screen-specific
    val ordersVM: OrdersViewModel = viewModel(
        factory = OrdersViewModelFactory(printerManager) // pass PrinterManager
    )

    // -----------------------------
    // START DATA LOADING (ONCE)
    // -----------------------------

    // Load first page of old (paginated) orders
    // This runs only once when Orders screen opens
    LaunchedEffect(Unit) {
        ordersVM.loadFirstPage()
    }

    // -----------------------------
    // STATE COLLECTION
    // -----------------------------

    // Orders loaded using pagination
    val pagedOrders by ordersVM.orders.collectAsState()

    // Orders coming from realtime Firestore listener
    // These are NEW orders arriving online
    val realtimeOrders by realtimeOrdersViewModel.realtimeOrders.collectAsState()

    // Loading state for pagination
    val loading by ordersVM.loading.collectAsState()

    // -----------------------------
    // COMBINE REALTIME + PAGINATION
    // -----------------------------

    // Realtime orders are shown FIRST
    // Older paginated orders are shown BELOW
    // Duplicate orders are removed using order ID
    val combinedOrders = remember(realtimeOrders, pagedOrders) {
        val realtimeIds = realtimeOrders.map { it.id }.toSet()

        val merged = realtimeOrders +
                pagedOrders.filter { it.id !in realtimeIds }

        // 🔽 SORT: newest order first
        merged.sortedByDescending { it.createdAt }
    }

    //use my sortorder
//    val combinedOrders = remember(realtimeOrders, pagedOrders) {
//        val realtimeIds = realtimeOrders.map { it.id }.toSet()
//        realtimeOrders + pagedOrders.filter { it.id !in realtimeIds }
//    }

    // -----------------------------
    // UI
    // -----------------------------

    Column(modifier = Modifier.padding(12.dp)) {

        Text("Orders", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(6.dp))

        when {
            // Show loading text only when nothing is loaded yet
            loading && combinedOrders.isEmpty() ->
                Text("Loading orders...")

            // No orders at all
            combinedOrders.isEmpty() ->
                Text("No orders found")

            // Orders available
            else -> {
                TableHeader()
                Spacer(Modifier.height(4.dp))

                // Show all orders (realtime + old)
                combinedOrders.forEach { order ->
                    OrderTableRow(
                        order = order,
                        onOrderClick = {
                            // Later you can open order detail screen here
                            println("OPEN ORDER DETAIL FOR: ${it.srno}")
                        },
                        onPrintClick = {
                            // Print using OrdersViewModel
                            ordersVM.printOrder(it)
                        }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Pagination controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { ordersVM.loadPrevPage() },
                        enabled = !loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("← Previous")
                    }

                    Button(
                        onClick = { ordersVM.loadNextPage() },
                        enabled = !loading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        )
                    ) {
                        Text("Next →")
                    }
                }
            }
        }
    }
}
