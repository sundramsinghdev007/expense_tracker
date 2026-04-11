// core/common/src/main/java/com/sundram/expense_tracker/common/DateUtils.kt
package com.sundram.expense_tracker.common

import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun LocalDate.toDisplayString(): String =
    format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))

fun LocalDate.isCurrentMonth(): Boolean {
    val now = LocalDate.now()
    return month == now.month && year == now.year
}

fun LocalDate.toStartOfMonth(): LocalDate = withDayOfMonth(1)

fun LocalDate.toEndOfMonth(): LocalDate = withDayOfMonth(lengthOfMonth())
