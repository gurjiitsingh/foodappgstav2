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

        Log.e("AUTO_PRINT", "🔥 onNewOrder called srno=${order.srno}")

        if (order.printed == true) {
            Log.d("AUTO_PRINT", "⛔ Already printed srno=${order.srno}")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {

            try {
                Log.d("AUTO_PRINT", "⏳ Waiting for items srno=${order.srno}")

                var itemsReady = false

                // ✅ Wait max 10 seconds only
                repeat(10) { attempt ->
                    val items = ordersRepository.getOrderProducts(order.id)
                    if (items.isNotEmpty()) {
                        Log.d("AUTO_PRINT", "✅ Items found at attempt=$attempt")
                        itemsReady = true
                        return@repeat
                    }
                    delay(1000)
                }

                if (!itemsReady) {
                    Log.e("AUTO_PRINT", "❌ No items found srno=${order.srno}")
                    return@launch
                }

                // ✅ PRINT
                Log.e("AUTO_PRINT", "🖨 Printing srno=${order.srno}")
                ordersViewModel.printOrder(order)

                // ✅ MARK PRINTED AFTER PRINT CALL
                ordersRepository.markOrderAsPrinted(order.id)

                Log.e("AUTO_PRINT", "✅ Auto print DONE srno=${order.srno}")

            } catch (e: Exception) {
                Log.e("AUTO_PRINT", "❌ Auto print failed", e)
            }
        }
    }
}
