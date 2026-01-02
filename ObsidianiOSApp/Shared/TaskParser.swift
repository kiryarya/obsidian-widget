import Foundation

struct Task: Identifiable {
    let id = UUID()
    let title: String
    let status: String
    let due: String?
    let priority: String?
    let filename: String
}

class TaskParser {

    static func parseTasks(from folderManager: FolderManager) -> [Task] {
        let fileUrls = folderManager.getAllMarkdownFiles()
        var tasks: [Task] = []

        // ファイルアクセス権限をここで一括確保
        guard let folderUrl = folderManager.getFolderUrl() else { return [] }
        guard folderUrl.startAccessingSecurityScopedResource() else { return [] }
        defer { folderUrl.stopAccessingSecurityScopedResource() }

        for url in fileUrls {
            if let content = try? String(contentsOf: url, encoding: .utf8) {
                if let task = parseTask(filename: url.lastPathComponent, content: content) {
                    // 完了していないタスクのみ追加
                    if task.status != "done" && task.status != "completed" {
                        tasks.add(task)
                    }
                }
            }
        }

        // 日付順にソート (期限なしは最後に)
        return tasks.sorted {
            ($0.due ?? "9999-99-99") < ($1.due ?? "9999-99-99")
        }
    }

    // テスト用にinternalに変更
    static func parseTask(filename: String, content: String) -> Task? {
        let lines = content.components(separatedBy: .newlines)

        // YAMLフロントマターの確認
        guard lines.count > 0 && lines[0].trimmingCharacters(in: .whitespaces) == "---" else {
            return nil
        }

        var title: String? = nil
        var status: String? = nil
        var due: String? = nil
        var priority: String? = nil

        var isFrontmatter = false
        var count = 0

        for line in lines {
            let trimmed = line.trimmingCharacters(in: .whitespaces)
            if trimmed == "---" {
                if count == 0 {
                    isFrontmatter = true
                } else {
                    isFrontmatter = false
                    break // フロントマター終了
                }
            }

            if isFrontmatter && count > 0 {
                if trimmed.starts(with: "title:") { title = parseValue(line: trimmed) }
                if trimmed.starts(with: "status:") { status = parseValue(line: trimmed) }
                if trimmed.starts(with: "due:") { due = parseValue(line: trimmed) }
                if trimmed.starts(with: "priority:") { priority = parseValue(line: trimmed) }
            }

            count += 1
            if count > 50 { break } // 読み込み制限
        }

        if let status = status {
            let finalTitle = title ?? filename.replacingOccurrences(of: ".md", with: "")
            return Task(title: finalTitle, status: status, due: due, priority: priority, filename: filename)
        }

        return nil
    }

    private static func parseValue(line: String) -> String {
        let parts = line.split(separator: ":", maxSplits: 1).map(String.init)
        guard parts.count > 1 else { return "" }

        var value = parts[1].trimmingCharacters(in: .whitespaces)

        if (value.hasPrefix("\"") && value.hasSuffix("\"")) || (value.hasPrefix("'") && value.hasSuffix("'")) {
            value.removeFirst()
            value.removeLast()
        }

        return value
    }
}

// 配列への追加用拡張
extension Array where Element == Task {
    mutating func add(_ task: Task) {
        self.append(task)
    }
}
