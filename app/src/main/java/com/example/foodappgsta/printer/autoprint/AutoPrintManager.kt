package com.it10x.foodappgstav2.printer

import android.util.Log
import com.it10x.foodappgstav2.data.models.OrderMasterData
import com.it10x.foodappgstav2.data.repository.OrdersRepository
import com.it10x.foodappgstav2.viewmodel.OrdersViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AutoPrintManager(
    private val ordersViewModel: OrdersViewModel,
    private val ordersRepository: OrdersRepository
) {

    fun onNewOrder(order: OrderMasterData) {

        if (order.printed == true) {
            Log.d("AUTO_PRINT", "Order ${order.srno} already printed")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
             delay(15000) // wait for Firestore writes
            try {

                // ⏳ WAIT until order items exist (CRITICAL FIX)
                var attempts = 0
                var hasItems = false

                while (attempts < 5 && !hasItems) {
                    val items = ordersRepository.getOrderProducts(order.id)
                    if (items.isNotEmpty()) {
                        hasItems = true
                        break
                    }
                    delay(7000) // wait for Firestore writes
                    attempts++
                    kotlinx.coroutines.delay(500) // wait 0.5 sec
                }

                if (!hasItems) {
                    Log.e("AUTO_PRINT", "No items found for order ${order.srno}")
                    return@launch
                }

                // ✅ Now safe to print
                ordersViewModel.printOrder(order)

                // ✅ Mark as printed
                ordersRepository.markOrderAsPrinted(order.id)

                Log.d("AUTO_PRINT", "Order ${order.srno} auto-printed")

            } catch (e: Exception) {
                Log.e("AUTO_PRINT", "Auto print failed", e)
            }
        }
    }
}
