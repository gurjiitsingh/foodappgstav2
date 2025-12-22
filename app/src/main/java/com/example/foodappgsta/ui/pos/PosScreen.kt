package com.it10x.foodappgstav2.ui.pos

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PosScreen(
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("POS Screen", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(30.dp))

        Button(onClick = { onOpenSettings() }) {
            Text("Printer Settings")
        }

        Spacer(Modifier.height(20.dp))

        // You will add printing buttons here later
        Text("POS functions go here...")
    }
}
