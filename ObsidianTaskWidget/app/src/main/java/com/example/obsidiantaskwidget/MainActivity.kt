package com.example.obsidiantaskwidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var pathTextView: TextView

    private val openDocumentTreeLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            // 権限を永続化
            val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, takeFlags)

            TaskRepository.saveFolderUri(this, uri)
            updatePathDisplay(uri)
            triggerWidgetUpdate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pathTextView = findViewById(R.id.tv_path)
        val selectButton: Button = findViewById(R.id.btn_select_folder)
        val refreshButton: Button = findViewById(R.id.btn_refresh)

        selectButton.setOnClickListener {
            openDocumentTreeLauncher.launch(null)
        }

        refreshButton.setOnClickListener {
            triggerWidgetUpdate()
        }

        val currentUri = TaskRepository.getFolderUri(this)
        if (currentUri != null) {
            updatePathDisplay(currentUri)
        }
    }

    private fun updatePathDisplay(uri: Uri) {
        pathTextView.text = getString(R.string.current_path, uri.path)
    }

    private fun triggerWidgetUpdate() {
        val intent = Intent(this, TaskWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
            ComponentName(application, TaskWidgetProvider::class.java)
        )
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
}
