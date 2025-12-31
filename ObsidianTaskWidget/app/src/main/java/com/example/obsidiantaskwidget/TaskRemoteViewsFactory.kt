package com.example.obsidiantaskwidget

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class TaskRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private var tasks: List<Task> = emptyList()

    override fun onCreate() {
        // 初期ロード
    }

    override fun onDataSetChanged() {
        // notifyAppWidgetViewDataChangedがトリガーされたときに呼び出されます
        // バックグラウンドスレッドで実行されます
        tasks = TaskRepository.getTasks(context)
    }

    override fun onDestroy() {
        tasks = emptyList()
    }

    override fun getCount(): Int {
        return tasks.size
    }

    override fun getViewAt(position: Int): RemoteViews {
        if (position >= tasks.size) return RemoteViews(context.packageName, R.layout.widget_item)

        val task = tasks[position]
        val views = RemoteViews(context.packageName, R.layout.widget_item)

        views.setTextViewText(R.id.item_title, task.title)

        val details = "Status: ${task.status} | Due: ${task.due ?: "None"}"
        views.setTextViewText(R.id.item_details, details)

        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }
}
