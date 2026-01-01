import WidgetKit
import SwiftUI

struct Provider: TimelineProvider {
    let folderManager = FolderManager.shared

    func placeholder(in context: Context) -> TaskEntry {
        TaskEntry(date: Date(), tasks: [
            Task(title: "サンプルタスク", status: "todo", due: "2023-10-27", priority: "high", filename: "sample.md")
        ])
    }

    func getSnapshot(in context: Context, completion: @escaping (TaskEntry) -> ()) {
        let entry = TaskEntry(date: Date(), tasks: [
            Task(title: "スナップショットタスク", status: "todo", due: "2023-10-27", priority: nil, filename: "snap.md")
        ])
        completion(entry)
    }

    func getTimeline(in context: Context, completion: @escaping (Timeline<TaskEntry>) -> ()) {
        // バックグラウンドでタスクを読み込む
        // App Groupの設定が正しくないと、ここでファイルにアクセスできないことに注意

        let tasks = TaskParser.parseTasks(from: folderManager)

        // 更新時刻
        let currentDate = Date()
        let entry = TaskEntry(date: currentDate, tasks: tasks)

        // 30分後に更新
        let nextUpdateDate = Calendar.current.date(byAdding: .minute, value: 30, to: currentDate)!
        let timeline = Timeline(entries: [entry], policy: .after(nextUpdateDate))

        completion(timeline)
    }
}

struct TaskEntry: TimelineEntry {
    let date: Date
    let tasks: [Task]
}

struct TaskWidgetEntryView : View {
    var entry: Provider.Entry

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            HStack {
                Text("Tasks")
                    .font(.headline)
                    .foregroundColor(.accentColor)
                Spacer()
                Text("\(entry.tasks.count)")
                    .font(.caption)
                    .foregroundColor(.gray)
            }
            .padding(.bottom, 4)

            if entry.tasks.isEmpty {
                Text("No tasks found")
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                ForEach(entry.tasks.prefix(4)) { task in
                    HStack(alignment: .top) {
                        Image(systemName: "circle")
                            .font(.system(size: 10))
                            .padding(.top, 4)

                        VStack(alignment: .leading) {
                            Text(task.title)
                                .font(.system(size: 12, weight: .medium))
                                .lineLimit(1)

                            HStack {
                                if let due = task.due {
                                    Text("Due: \(due)")
                                        .font(.system(size: 9))
                                        .foregroundColor(.red)
                                }
                                if let priority = task.priority {
                                    Text(priority)
                                        .font(.system(size: 9))
                                        .foregroundColor(.orange)
                                }
                            }
                        }
                    }
                    .padding(.vertical, 1)
                }
            }
            Spacer()
        }
        .padding()
    }
}

@main
struct TaskWidget: Widget {
    let kind: String = "TaskWidget"

    var body: some WidgetConfiguration {
        StaticConfiguration(kind: kind, provider: Provider()) { entry in
            TaskWidgetEntryView(entry: entry)
        }
        .configurationDisplayName("Obsidian Tasks")
        .description("Shows open tasks from your Tasknotes.")
        .supportedFamilies([.systemSmall, .systemMedium])
    }
}
