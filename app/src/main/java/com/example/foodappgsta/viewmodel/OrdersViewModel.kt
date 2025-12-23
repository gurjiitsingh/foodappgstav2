package com.it10x.foodappgstav2.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.it10x.foodappgstav2.data.PrinterRole
import com.it10x.foodappgstav2.data.models.OrderMasterData
import com.it10x.foodappgstav2.data.models.OrderProductData
import com.it10x.foodappgstav2.data.repository.OrdersRepository
import com.it10x.foodappgstav2.printer.PrinterManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val printerManager: PrinterManager
) : ViewModel() {

    private val repo = OrdersRepository()

    private val _orders = MutableStateFlow<List<OrderMasterData>>(emptyList())
    val orders: StateFlow<List<OrderMasterData>> = _orders

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val limit = 10

    // -----------------------------
    // PRINT ORDER (KITCHEN + BILLING)
    // -----------------------------
fun printOrder(order: OrderMasterData) {
    viewModelScope.launch {
        val items = repo.getOrderProducts(order.id)

        if (items.isEmpty()) {
            Log.e("PRINT", "No items for order ${order.srno}")
            return@launch
        }

        val billingReceipt = buildBillingReceipt(order, items)
        val kitchenReceipt = buildKitchenReceipt(order, items)


        // ✅ BILLING
        printerManager.printText(PrinterRole.BILLING, billingReceipt) { success ->
            Log.d("PRINT", "Billing print success=$success for order ${order.srno}")
        }

        // ✅ KITCHEN
//        printerManager.printText(PrinterRole.KITCHEN, kitchenReceipt) { success ->
//            Log.d("PRINT", "Kitchen print success=$success for order ${order.srno}")
//
//        }


    }
}





    fun loadFirstPage() {
        viewModelScope.launch {
            _loading.value = true
            _orders.value = repo.getFirstPage(limit.toLong())
            _loading.value = false
        }
    }

    fun loadNextPage() {
        viewModelScope.launch {
            _loading.value = true
            _orders.value = repo.getNextPage(limit.toLong())
            _loading.value = false
        }
    }

    fun loadPrevPage() {
        viewModelScope.launch {
            _loading.value = true
            _orders.value = repo.getPrevPage(limit.toLong())
            _loading.value = false
        }
    }

    // -----------------------------
// BILLING RECEIPT (SAFE FORMAT)
// -----------------------------

    private fun buildBillingReceipt(
        order: OrderMasterData,
        items: List<OrderProductData>
    ): String {
//THIS FUNCITON FROM KITCHEN
        val alignLeft = "\u001B\u0061\u0000"

        val itemsBlock = if (items.isEmpty()) {
            "No items"
        } else {
            items.joinToString("\n") { item ->
                "${item.quantity.toString().padEnd(3)} ${item.name}"
            }
        }

        return buildString {
            append(alignLeft)
            append(
                """
******* BILL *******

Order No : ${order.srno}
------------------------
$itemsBlock
------------------------


""".trimIndent()
            )
        }
    }

    private fun buildBillingReceipt2(
        order: OrderMasterData,
        items: List<OrderProductData>
    ): String {

        // ESC/POS force left align
        val alignLeft = "\u001B\u0061\u0000"

        val orderNoLine =
            "Order No : ${btSafe(order.srno.toString(), 12)}"

        val customerLine =
            "Customer : ${btSafe(order.customerName.ifBlank { "Walk-in" }, 18)}"

        val itemsBlock = if (items.isEmpty()) {
            "No items found"
        } else {
            val header =
                "QTY".padEnd(4) +
                        "ITEM".padEnd(16) +
                        "PRICE".padStart(6) +
                        "TOTAL".padStart(6)

            val divider = "-".repeat(32)

            val lines = items.joinToString("\n") { item ->
                val qty = item.quantity.toString().padEnd  (4)
                val name = item.name.take(16).padEnd(16)
                val price = formatAmount(toDouble(item.price)).padStart(6)
                val total = formatAmount(toDouble(item.itemSubtotal)).padStart(6)

                qty + name + price + total
            }

            "$header\n$divider\n$lines"
        }

        return buildString {
            append(alignLeft)
            append(
                """
------------------------------
FOOD APP
------------------------------


------------------------------

------------------------------

------------------------------
Thank You!


""".trimIndent()
            )
        }
    }

    private fun buildBillingReceipt1(
        order: OrderMasterData,
        items: List<OrderProductData>
    ): String {

        // ESC/POS left align
        val alignLeft = "\u001B\u0061\u0000"

        return buildString {
            append(alignLeft)
            append(
                """
------------------------------
FOOD APP TEST
------------------------------
Order No : 123
Customer : TEST
------------------------------
QTY ITEM            PRICE TOTAL
--------------------------------
1   Burger          100.00 100.00
2   Fries            50.00 100.00
--------------------------------
TOTAL : 200.00
------------------------------
Thank You!


""".trimIndent()
            )
        }
    }






    // -----------------------------
// KITCHEN SLIP (SAFE FORMAT)
// -----------------------------
    private fun buildKitchenReceipt(
        order: OrderMasterData,
        items: List<OrderProductData>
    ): String {

        val alignLeft = "\u001B\u0061\u0000"

        val itemsBlock = if (items.isEmpty()) {
            "No items"
        } else {
            items.joinToString("\n") { item ->
                "${item.quantity.toString().padEnd(3)} ${item.name}"
            }
        }

        return buildString {
            append(alignLeft)
            append(
                """
******** KITCHEN ********
Order No : ${order.srno}
------------------------
$itemsBlock
------------------------


""".trimIndent()
            )
        }
    }


    // -----------------------------
    // HELPERS
    // -----------------------------
    private fun formatAmount(value: Double?): String =
        "%.2f".format(value ?: 0.0)

    private fun toDouble(value: Any?): Double =
        when (value) {
            is Double -> value
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            is Float -> value.toDouble()
            is String -> value.toDoubleOrNull() ?: 0.0
            else -> 0.0
        }

    private fun btSafe(text: String, max: Int): String {
        return text
            .replace(Regex("[^A-Za-z0-9 ]"), "") // remove Unicode
            .trim()
            .take(max)
    }
}
