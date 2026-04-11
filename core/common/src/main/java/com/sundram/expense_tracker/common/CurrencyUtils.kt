// core/common/src/main/java/com/sundram/expense_tracker/common/CurrencyUtils.kt
package com.sundram.expense_tracker.common

fun formatAmount(amount: Double, currencyCode: String): String {
    val symbol = when (currencyCode) {
        "INR" -> "₹"
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        else  -> currencyCode
    }
    return "$symbol%.2f".format(amount)
}
