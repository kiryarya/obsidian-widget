import SwiftUI
import UniformTypeIdentifiers

struct ContentView: View {
    @StateObject private var folderManager = FolderManager.shared
    @State private var showDocumentPicker = false
    @State private var memoText = ""
    @State private var showSaveMessage = false
    @State private var saveMessage = ""

    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                // ステータス表示
                if let path = folderManager.currentPath {
                    Text("選択中のフォルダ:")
                        .font(.caption)
                        .foregroundColor(.gray)
                    Text(path)
                        .font(.footnote)
                        .lineLimit(1)
                        .truncationMode(.middle)
                        .padding(.horizontal)
                } else {
                    Text("Obsidianのフォルダが選択されていません")
                        .foregroundColor(.red)
                }

                Button("フォルダを選択") {
                    showDocumentPicker = true
                }
                .buttonStyle(.bordered)

                Divider()
                    .padding(.vertical)

                // クイックメモ入力
                VStack(alignment: .leading) {
                    Text("クイックメモ (Thino)")
                        .font(.headline)

                    TextEditor(text: $memoText)
                        .frame(height: 100)
                        .overlay(
                            RoundedRectangle(cornerRadius: 8)
                                .stroke(Color.gray.opacity(0.5), lineWidth: 1)
                        )

                    Button(action: saveMemo) {
                        Text("保存")
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(8)
                    }
                    .disabled(memoText.isEmpty || folderManager.currentPath == nil)
                }
                .padding()

                Spacer()
            }
            .navigationTitle("Obsidian Tools")
            .sheet(isPresented: $showDocumentPicker) {
                DocumentPicker(folderManager: folderManager)
            }
            .alert(saveMessage, isPresented: $showSaveMessage) {
                Button("OK", role: .cancel) { }
            }
        }
    }

    func saveMemo() {
        folderManager.appendMemo(text: memoText) { success in
            DispatchQueue.main.async {
                if success {
                    saveMessage = "保存しました"
                    memoText = "" // 入力をクリア
                } else {
                    saveMessage = "保存に失敗しました"
                }
                showSaveMessage = true
            }
        }
    }
}

// UIDocumentPickerViewControllerのラッパー
struct DocumentPicker: UIViewControllerRepresentable {
    var folderManager: FolderManager

    func makeUIViewController(context: Context) -> UIDocumentPickerViewController {
        let picker = UIDocumentPickerViewController(forOpeningContentTypes: [.folder], asCopy: false)
        picker.delegate = context.coordinator
        picker.allowsMultipleSelection = false
        return picker
    }

    func updateUIViewController(_ uiViewController: UIDocumentPickerViewController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, UIDocumentPickerDelegate {
        var parent: DocumentPicker

        init(_ parent: DocumentPicker) {
            self.parent = parent
        }

        func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
            guard let url = urls.first else { return }
            // セキュリティスコープでアクセス可能なURLを保存
            parent.folderManager.saveBookmark(for: url)
        }
    }
}
