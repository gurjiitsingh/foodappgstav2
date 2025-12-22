package com.it10x.foodappgstav2.printer

import com.it10x.foodappgstav2.data.models.OrderMasterData

object ReceiptFormatter {

    fun formatOrder(order: OrderMasterData): String {
        return """
            ----------------------------
            FOOD APP GST
            ----------------------------
            Order No : ${order.srno}
            Customer : ${order.customerName.ifBlank { "Walk-in" }}
            Time     : ${order.time}
            ----------------------------
            TOTAL    : ${order.finalGrandTotal}
            ----------------------------
            Thank You!
            
            
        """.trimIndent()
    }
}
