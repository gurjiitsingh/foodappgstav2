package com.it10x.foodappgstav2.ui.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

@Composable
fun TableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFECECEC))
            .padding(vertical = 4.dp, horizontal = 6.dp)
    ) {
        Text("Order#", modifier = Modifier.weight(0.20f), style = MaterialTheme.typography.bodySmall)
        Text("Customer", modifier = Modifier.weight(0.40f), style = MaterialTheme.typography.bodySmall)
        Text("Time", modifier = Modifier.weight(0.25f), style = MaterialTheme.typography.bodySmall)
        Text("Total", modifier = Modifier.weight(0.15f), style = MaterialTheme.typography.bodySmall)
    }
}
