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

            // 🧾 BILLING RECEIPT
            val billingReceipt = buildBillingReceipt(order, items)

            // 🍳 KITCHEN SLIP
            val kitchenReceipt = buildKitchenReceipt(order, items)

            // ---- PRINT KITCHEN ----
            printerManager.printText(
                role = PrinterRole.KITCHEN,
                text = kitchenReceipt
            ) {
//                success ->
//                Log.d("PRINT", "Kitchen print: $success")
            }

            // ---- PRINT BILLING ----
            printerManager.printText(
                role = PrinterRole.BILLING,
                text = billingReceipt
            ) {
//                success ->
//                Log.d("PRINT", "Billing print: $success")
            }
        }
    }

    private fun PrinterManager.printText(
        role: PrinterRole,
        text: String,
        function: Any
    ) {
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
    // BILLING RECEIPT (FULL)
    // -----------------------------
    private fun buildBillingReceipt(
        order: OrderMasterData,
        items: List<OrderProductData>
    ): String {

        val alignLeft = "\u001B\u0061\u0000"

        val itemsBlock = items.joinToString("\n") { item ->
            "${item.quantity}  ${item.name.take(16).padEnd(16)}  ${formatAmount(toDouble(item.itemSubtotal))}"
        }

        return buildString {
            append(alignLeft)
            append(
                """
------------------------------
FOOD APP
------------------------------
Order No : ${order.srno}
Customer : ${order.customerName.ifBlank { "Walk-in" }}
------------------------------
$itemsBlock
------------------------------
TOTAL : ${formatAmount(toDouble(order.grandTotal))}
------------------------------
Thank You!
                

""".trimIndent()
            )
        }
    }

    // -----------------------------
    // KITCHEN SLIP (ITEMS ONLY)
    // -----------------------------
    private fun buildKitchenReceipt(
        order: OrderMasterData,
        items: List<OrderProductData>
    ): String {

        val alignLeft = "\u001B\u0061\u0000"

        val itemsBlock = items.joinToString("\n") { item ->
            "${item.quantity} x ${item.name}"
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
}
