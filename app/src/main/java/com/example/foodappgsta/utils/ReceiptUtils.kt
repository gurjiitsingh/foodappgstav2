package com.it10x.foodappgstav2.utils

fun formatAmount(value: Double?): String {
    return "%.2f".format(value ?: 0.0)
}
