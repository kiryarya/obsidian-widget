package com.example.obsidianmemowidget

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var pathTextView: TextView

    // フォルダを選択するためのSAFランチャー
    private val openDocumentTreeLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            // 権限を永続化
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)

            // URIを保存
            saveFolderUri(uri)
            updatePathDisplay(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pathTextView = findViewById(R.id.tv_path)
        val selectButton: Button = findViewById(R.id.btn_select_folder)

        selectButton.setOnClickListener {
            openDocumentTreeLauncher.launch(null)
        }

        val currentUri = getFolderUri()
        if (currentUri != null) {
            updatePathDisplay(currentUri)
        }
    }

    private fun updatePathDisplay(uri: Uri) {
        pathTextView.text = getString(R.string.current_path, uri.path)
    }

    private fun saveFolderUri(uri: Uri) {
        val prefs = getSharedPreferences("obsidian_memo_prefs", MODE_PRIVATE)
        prefs.edit().putString("folder_uri", uri.toString()).apply()
    }

    private fun getFolderUri(): Uri? {
        val prefs = getSharedPreferences("obsidian_memo_prefs", MODE_PRIVATE)
        val uriString = prefs.getString("folder_uri", null) ?: return null
        return Uri.parse(uriString)
    }
}
