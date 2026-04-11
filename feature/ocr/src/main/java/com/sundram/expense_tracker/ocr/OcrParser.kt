// feature/ocr/src/main/java/com/sundram/expense_tracker/ocr/OcrParser.kt
package com.sundram.expense_tracker.ocr

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Pure Kotlin object — zero Android imports.
 * All functions are deterministic and side-effect free.
 */
object OcrParser {

    private val amountRegex = Regex("""[₹Rs.\s]*(\d+(?:\.\d{1,2})?)""")

    private val datePatterns = listOf(
        Regex("""(\d{2})[/\-](\d{2})[/\-](\d{4})"""),         // DD/MM/YYYY or DD-MM-YYYY
        Regex("""([A-Za-z]{3})\s+(\d{1,2})\s+(\d{4})"""),     // MMM DD YYYY
    )

    /**
     * Extracts the first monetary amount found in [text].
     * Returns null if [text] is blank or no amount pattern is matched.
     */
    fun parseAmount(text: String): Double? {
        if (text.isBlank()) return null
        return amountRegex.find(text)
            ?.groupValues
            ?.getOrNull(1)
            ?.toDoubleOrNull()
    }

    /**
     * Extracts the first recognisable date from [text].
     * Supports DD/MM/YYYY, DD-MM-YYYY, and "MMM DD YYYY" formats.
     * Returns null if no date pattern is matched or the matched values are invalid.
     */
    fun parseDate(text: String): LocalDate? {
        // Try DD/MM/YYYY or DD-MM-YYYY
        datePatterns[0].find(text)?.let { match ->
            val (day, month, year) = match.destructured
            return runCatching {
                LocalDate.of(year.toInt(), month.toInt(), day.toInt())
            }.getOrNull()
        }

        // Try MMM DD YYYY
        datePatterns[1].find(text)?.let { match ->
            val (monthStr, day, year) = match.destructured
            return runCatching {
                LocalDate.parse(
                    "$monthStr $day $year",
                    DateTimeFormatter.ofPattern("MMM d yyyy", Locale.ENGLISH),
                )
            }.getOrNull()
        }

        return null
    }

    /**
     * Returns the first non-blank line that starts with an uppercase character,
     * treating it as the merchant / receipt title.
     * Returns null if [text] is blank or no matching line exists.
     */
    fun parseMerchantName(text: String): String? {
        if (text.isBlank()) return null
        return text.lines()
            .firstOrNull { line -> line.isNotBlank() && line.first().isUpperCase() }
            ?.trim()
    }
}
