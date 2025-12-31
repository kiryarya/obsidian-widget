package com.example.obsidiantaskwidget

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import java.io.BufferedReader
import java.io.InputStreamReader

object TaskRepository {

    private const val PREFS_NAME = "obsidian_task_widget_prefs"
    private const val KEY_FOLDER_URI = "folder_uri"

    fun saveFolderUri(context: Context, uri: Uri) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FOLDER_URI, uri.toString()).apply()
    }

    fun getFolderUri(context: Context): Uri? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val uriString = prefs.getString(KEY_FOLDER_URI, null) ?: return null
        return Uri.parse(uriString)
    }

    fun getTasks(context: Context): List<Task> {
        val uri = getFolderUri(context) ?: return emptyList()
        val folder = DocumentFile.fromTreeUri(context, uri) ?: return emptyList()

        if (!folder.canRead()) return emptyList()

        val tasks = mutableListOf<Task>()
        val files = folder.listFiles()

        for (file in files) {
            if (file.name?.endsWith(".md") == true) {
                val task = parseTask(context, file)
                if (task != null && task.status != "done" && task.status != "completed") {
                    tasks.add(task)
                }
            }
        }
        return tasks.sortedBy { it.due ?: "9999-99-99" }
    }

    private fun parseTask(context: Context, file: DocumentFile): Task? {
        try {
            val inputStream = context.contentResolver.openInputStream(file.uri) ?: return null
            val reader = BufferedReader(InputStreamReader(inputStream))

            var line: String? = reader.readLine()
            if (line?.trim() != "---") {
                reader.close()
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
            reader.close()
            inputStream.close()

            if (status != null) {
                // YAMLにタイトルがない場合は、拡張子なしのファイル名を使用します
                val finalTitle = title ?: file.name?.removeSuffix(".md") ?: "Untitled"
                return Task(finalTitle, status, due, priority, file.name ?: "")
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
