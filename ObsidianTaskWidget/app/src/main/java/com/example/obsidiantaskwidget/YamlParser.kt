package com.example.obsidiantaskwidget

import java.io.BufferedReader
import java.io.StringReader

object YamlParser {

    fun parseTask(filename: String, content: String): Task? {
        return parseTask(filename, BufferedReader(StringReader(content)))
    }

    fun parseTask(filename: String, reader: BufferedReader): Task? {
        try {
            var line: String? = reader.readLine()
            if (line?.trim() != "---") {
                return null // 有効なYAMLフロントマターの開始ではありません
            }

            var title: String? = null
            var status: String? = null
            var due: String? = null
            var priority: String? = null

            // 巨大なファイル全体を読み込むのを避けるため、最初の50行に制限します
            var count = 0
            while (count < 50) {
                line = reader.readLine()
                if (line == null || line.trim() == "---") break

                val trimLine = line.trim()
                when {
                    trimLine.startsWith("title:") -> title = parseValue(trimLine)
                    trimLine.startsWith("status:") -> status = parseValue(trimLine)
                    trimLine.startsWith("due:") -> due = parseValue(trimLine)
                    trimLine.startsWith("priority:") -> priority = parseValue(trimLine)
                }
                count++
            }

            if (status != null) {
                // YAMLにタイトルがない場合は、拡張子なしのファイル名を使用します
                val finalTitle = title ?: filename.removeSuffix(".md")
                return Task(finalTitle, status, due, priority, filename)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun parseValue(line: String): String {
        // "key: " と引用符を削除します
        val parts = line.split(":", limit = 2)
        if (parts.size < 2) return ""
        var value = parts[1].trim()
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length - 1)
        } else if (value.startsWith("'") && value.endsWith("'")) {
             value = value.substring(1, value.length - 1)
        }
        return value
    }
}
