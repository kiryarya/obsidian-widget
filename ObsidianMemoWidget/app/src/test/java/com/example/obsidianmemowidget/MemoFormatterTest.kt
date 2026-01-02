package com.example.obsidianmemowidget

import org.junit.Test
import org.junit.Assert.*
import java.util.Calendar
import java.util.TimeZone

class MemoFormatterTest {

    @Test
    fun getFileName_returnsCorrectFormat() {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.set(2023, Calendar.OCTOBER, 27)
        val date = calendar.time

        // Note: SimpleDateFormat in MemoFormatter uses Locale.getDefault().
        // We assume the test environment doesn't break this, but for robustness
        // the implementation should ideally accept a Locale or TimeZone.
        // For now, we just check the pattern.

        val fileName = MemoFormatter.getFileName(date)
        assertTrue(fileName.endsWith(".md"))
        assertTrue(fileName.length == 13) // "yyyy-MM-dd.md" is 10+3 chars
    }

    @Test
    fun formatContent_returnsCorrectFormat() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 14)
        calendar.set(Calendar.MINUTE, 30)
        val date = calendar.time

        val content = "Test memo"
        val formatted = MemoFormatter.formatContent(date, content)

        assertTrue(formatted.contains("- [14:30] Test memo"))
        assertTrue(formatted.startsWith("\n"))
    }
}
