package com.example.obsidianmemowidget

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuickCaptureActivity : AppCompatActivity() {

    private lateinit var memoInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quick_capture)

        memoInput = findViewById(R.id.et_memo)
        val btnSave: Button = findViewById(R.id.btn_save)
        val btnCancel: Button = findViewById(R.id.btn_cancel)

        btnSave.setOnClickListener {
            val text = memoInput.text.toString()
            if (text.isNotBlank()) {
                saveMemo(text)
            } else {
                finish()
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun saveMemo(text: String) {
        val folderUri = getFolderUri()
        if (folderUri == null) {
            Toast.makeText(this, R.string.no_path_selected, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        val folder = DocumentFile.fromTreeUri(this, folderUri)
        if (folder == null || !folder.canWrite()) {
            Toast.makeText(this, "Cannot write to folder", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 今日の日付を取得 (YYYY-MM-DD)
        val date = Date()
        val fileName = MemoFormatter.getFileName(date)

        // ファイルを探す、なければ作成する
        var file = folder.findFile(fileName)
        if (file == null) {
            file = folder.createFile("text/markdown", fileName)
        }

        if (file != null && file.canWrite()) {
            try {
                val outputStream = contentResolver.openOutputStream(file.uri, "wa") // "wa" for write append
                if (outputStream != null) {
                    // Thino形式: - [HH:mm] Content
                    val contentToAppend = MemoFormatter.formatContent(date, text)

                    outputStream.write(contentToAppend.toByteArray())
                    outputStream.close()

                    Toast.makeText(this, getString(R.string.success_message, fileName), Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    throw Exception("Stream is null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, R.string.error_message, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Cannot write to file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFolderUri(): Uri? {
        val prefs = getSharedPreferences("obsidian_memo_prefs", MODE_PRIVATE)
        val uriString = prefs.getString("folder_uri", null) ?: return null
        return Uri.parse(uriString)
    }
}
