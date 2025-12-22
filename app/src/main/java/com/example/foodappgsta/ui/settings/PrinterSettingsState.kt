package com.it10x.foodappgstav2.ui.settings

import com.it10x.foodappgstav2.data.PrinterConfig
import com.it10x.foodappgstav2.data.PrinterRole

data class PrinterSettingsState(
    val printers: Map<PrinterRole, PrinterConfig> = emptyMap()
)
