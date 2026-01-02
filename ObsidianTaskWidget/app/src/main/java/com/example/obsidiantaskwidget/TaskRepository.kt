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
            val fileName = file.name
            if (fileName != null && fileName.endsWith(".md")) {
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

            // Delegate parsing to pure Kotlin object
            val task = YamlParser.parseTask(file.name ?: "Untitled", reader)

            reader.close()
            inputStream.close()

            return task
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
