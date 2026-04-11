// feature/ocr/src/test/java/com/sundram/expense_tracker/ocr/OcrParserTest.kt
package com.sundram.expense_tracker.ocr

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate

class OcrParserTest {

    // parseAmount

    @Test fun `returns 250_00 for rupee symbol 250_00`() =
        assertEquals(250.00, OcrParser.parseAmount("₹250.00")!!, 0.001)

    @Test fun `returns 1500 for Total 1500`() {
        val result = OcrParser.parseAmount("Total: 1500")
        assertNotNull(result); assertEquals(1500.0, result!!, 0.001)
    }

    @Test fun `returns 99_99 for Rs 99_99`() {
        val result = OcrParser.parseAmount("Rs. 99.99")
        assertNotNull(result); assertEquals(99.99, result!!, 0.001)
    }

    @Test fun `returns null for no amount here`() =
        assertNull(OcrParser.parseAmount("no amount here"))

    @Test fun `returns null for empty string - amount`() =
        assertNull(OcrParser.parseAmount(""))

    // parseDate

    @Test fun `parses DD slash MM slash YYYY`() =
        assertEquals(LocalDate.of(2024, 6, 15), OcrParser.parseDate("Date: 15/06/2024"))

    @Test fun `parses DD dash MM dash YYYY`() =
        assertEquals(LocalDate.of(2024, 6, 15), OcrParser.parseDate("Date: 15-06-2024"))

    @Test fun `parses MMM DD YYYY`() =
        assertEquals(LocalDate.of(2024, 6, 15), OcrParser.parseDate("Jun 15 2024"))

    @Test fun `returns null for unparseable string`() =
        assertNull(OcrParser.parseDate("not a date at all"))

    @Test fun `returns null for empty string - date`() =
        assertNull(OcrParser.parseDate(""))

    // parseMerchantName

    @Test fun `extracts first capitalised line`() {
        val text = "STARBUCKS COFFEE\nDate: 01/01/2024\nTotal: 250.00"
        assertEquals("STARBUCKS COFFEE", OcrParser.parseMerchantName(text))
    }

    @Test fun `skips blank lines before capitalised line`() {
        val text = "\n\nZomato Order\nTotal: 450"
        assertEquals("Zomato Order", OcrParser.parseMerchantName(text))
    }

    @Test fun `returns null for blank input`() =
        assertNull(OcrParser.parseMerchantName(""))

    @Test fun `returns null for whitespace only`() =
        assertNull(OcrParser.parseMerchantName("   \n  \n  "))
}
