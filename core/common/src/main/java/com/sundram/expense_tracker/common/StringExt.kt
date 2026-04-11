// core/common/src/main/java/com/sundram/expense_tracker/common/StringExt.kt
package com.sundram.expense_tracker.common

fun String.isValidAmount(): Boolean =
    this.isNotBlank() && this.toDoubleOrNull()?.let { it > 0 } == true
