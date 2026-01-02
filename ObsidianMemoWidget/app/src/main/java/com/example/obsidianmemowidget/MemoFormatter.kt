package com.example.obsidianmemowidget

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MemoFormatter {
    fun getFileName(date: Date): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(date) + ".md"
    }

    fun formatContent(date: Date, text: String): String {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeString = timeFormat.format(date)
        return "\n- [$timeString] $text"
    }
}
