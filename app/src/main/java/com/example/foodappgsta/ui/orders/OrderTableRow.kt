package com.it10x.foodappgstav2.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.it10x.foodappgstav2.data.models.OrderMasterData
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Print
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import com.it10x.foodappgstav2.utils.formatAmount2
import java.text.NumberFormat
import java.util.Locale
@Composable
fun OrderTableRow(
    order: OrderMasterData,
    onOrderClick: (OrderMasterData) -> Unit,
    onPrintClick: (OrderMasterData) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 4.dp, horizontal = 6.dp)
    ) {
        // ----- Order Number (Clickable) -----
        Text(
            text = order.srno.toString(),
            modifier = Modifier
                .weight(0.20f)
                .clickable { onOrderClick(order) },
            style = MaterialTheme.typography.bodySmall
        )

        // ----- Customer -----
        Text(
            text = order.customerName.ifBlank { "Walk-in" },
            modifier = Modifier.weight(0.40f),
            style = MaterialTheme.typography.bodySmall
        )

        // ----- Time -----
        Text(
            text = order.time,
            modifier = Modifier.weight(0.25f),
            style = MaterialTheme.typography.bodySmall
        )

        // ----- Total -----
        Text(
            text = formatAmount2(order.finalGrandTotal),
            modifier = Modifier.weight(0.15f),
            style = MaterialTheme.typography.bodySmall
        )

        // ----- Print Button -----
        TextButton(
            onClick = { onPrintClick(order) }
        ) {
            Icon(
                imageVector = Icons.Filled.Print,
                contentDescription = "Print",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Top)
            )
        }
    }





}
