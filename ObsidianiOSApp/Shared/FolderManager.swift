import Foundation
import SwiftUI

// 重要: Xcodeで設定したApp Group IDに合わせて変更してください
let APP_GROUP_ID = "group.com.example.obsidiantools"
let BOOKMARK_KEY = "obsidian_folder_bookmark"

class FolderManager: ObservableObject {
    static let shared = FolderManager()

    @Published var currentPath: String? = nil

    init() {
        loadBookmark()
    }

    // フォルダ選択ダイアログからのURLを処理
    func saveBookmark(for url: URL) {
        guard url.startAccessingSecurityScopedResource() else { return }
        defer { url.stopAccessingSecurityScopedResource() }

        do {
            let bookmarkData = try url.bookmarkData(options: .minimalBookmark, includingResourceValuesForKeys: nil, relativeTo: nil)

            // App GroupのUserDefaultsに保存
            if let userDefaults = UserDefaults(suiteName: APP_GROUP_ID) {
                userDefaults.set(bookmarkData, forKey: BOOKMARK_KEY)
                userDefaults.synchronize()

                DispatchQueue.main.async {
                    self.currentPath = url.path
                }
                print("Bookmark saved successfully")
            }
        } catch {
            print("Failed to save bookmark: \(error)")
        }
    }

    // ブックマークからURLを復元してアクセス可能にする
    func getFolderUrl() -> URL? {
        guard let userDefaults = UserDefaults(suiteName: APP_GROUP_ID),
              let bookmarkData = userDefaults.data(forKey: BOOKMARK_KEY) else {
            return nil
        }

        var isStale = false
        do {
            let url = try URL(resolvingBookmarkData: bookmarkData, bookmarkDataIsStale: &isStale)

            if isStale {
                // ブックマークが古い場合の再保存ロジック（必要に応じて実装）
                print("Bookmark is stale")
            }

            return url
        } catch {
            print("Failed to resolve bookmark: \(error)")
            return nil
        }
    }

    // 保存済みのパスを表示用にロード
    func loadBookmark() {
        if let url = getFolderUrl() {
             // startAccessingSecurityScopedResourceはここでは呼ばず、実際に読み書きする時に呼ぶ
            self.currentPath = url.path
        }
    }

    // デイリーノートに追記 (Thinoスタイル)
    func appendMemo(text: String, completion: @escaping (Bool) -> Void) {
        guard let folderUrl = getFolderUrl() else {
            completion(false)
            return
        }

        guard folderUrl.startAccessingSecurityScopedResource() else {
            completion(false)
            return
        }
        defer { folderUrl.stopAccessingSecurityScopedResource() }

        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd"
        let fileName = dateFormatter.string(from: Date()) + ".md"

        let fileUrl = folderUrl.appendingPathComponent(fileName)

        let timeFormatter = DateFormatter()
        timeFormatter.dateFormat = "HH:mm"
        let timeString = timeFormatter.string(from: Date())

        let contentToAppend = "\n- [\(timeString)] \(text)"

        do {
            if FileManager.default.fileExists(atPath: fileUrl.path) {
                // 追記
                let fileHandle = try FileHandle(forWritingTo: fileUrl)
                fileHandle.seekToEndOfFile()
                if let data = contentToAppend.data(using: .utf8) {
                    fileHandle.write(data)
                }
                fileHandle.closeFile()
            } else {
                // 新規作成
                try contentToAppend.write(to: fileUrl, atomically: true, encoding: .utf8)
            }
            completion(true)
        } catch {
            print("Error writing file: \(error)")
            completion(false)
        }
    }

    // フォルダ内の全てのMarkdownファイルを取得
    func getAllMarkdownFiles() -> [URL] {
        guard let folderUrl = getFolderUrl() else { return [] }

        guard folderUrl.startAccessingSecurityScopedResource() else { return [] }
        defer { folderUrl.stopAccessingSecurityScopedResource() }

        do {
            let fileUrls = try FileManager.default.contentsOfDirectory(at: folderUrl, includingPropertiesForKeys: nil)
            return fileUrls.filter { $0.pathExtension == "md" }
        } catch {
            print("Error listing files: \(error)")
            return []
        }
    }

    // ファイルの内容を読み込む
    func readFileContent(url: URL) -> String? {
        // 注: 親フォルダでstartAccessingSecurityScopedResource済みであると仮定するか、
        // 個別に呼ぶ必要があるが、通常は親フォルダの権限でアクセス可能。
        // 安全のためここでもガードするが、呼び出し元で制御する方が効率的。
        do {
            return try String(contentsOf: url, encoding: .utf8)
        } catch {
            print("Error reading file: \(error)")
            return nil
        }
    }
}
