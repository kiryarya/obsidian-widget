# Obsidian Tools for iOS

このプロジェクトは、Obsidianのタスク表示（Tasknotes）とクイックメモ（Thino）の機能を提供するiOSアプリケーションのソースコードです。
Android版と同じ機能をiOS（Swift/SwiftUI）で実装するためのテンプレートです。

## 必要条件

*   macOS
*   Xcode 14.0以上
*   Apple Developer Account（App Groupsを使用するため、無料アカウントでも実機転送は可能ですが、設定が必要です）

## セットアップ手順

iOSでは、メインアプリとウィジェット間でデータを共有するために **App Groups** の設定が必須です。また、外部フォルダ（Obsidian Vault）にアクセスするために **Security Scoped Bookmarks** を使用します。

1.  **Xcodeプロジェクトの作成**:
    *   Xcodeを開き、新しい "App" プロジェクトを作成します（名前例: `ObsidianTools`）。
    *   Interfaceは "SwiftUI" を選択します。
2.  **ファイルの追加**:
    *   このフォルダ（`ObsidianiOSApp`）内のファイルを、Xcodeプロジェクトの適切な場所にドラッグ＆ドロップして追加します。
3.  **App Groupの設定**:
    *   プロジェクト設定の "Signing & Capabilities" タブを開きます。
    *   "+ Capability" をクリックし、"App Groups" を追加します。
    *   新しいグループを作成します（例: `group.com.yourname.obsidiantools`）。
    *   **重要**: `Shared/FolderManager.swift` 内の `APP_GROUP_ID` 定数を、作成したグループIDに変更してください。
4.  **Widget Extensionの追加**:
    *   Xcodeのメニューから `File > New > Target` を選択します。
    *   "Widget Extension" を選択し、追加します（名前例: `TaskWidget`）。
    *   Widget Targetの "Signing & Capabilities" にも、**同じ App Group** を追加します。
    *   Widget Targetの "Build Phases" > "Compile Sources" に、`Shared` フォルダ内のファイル（`FolderManager.swift`, `TaskParser.swift` など）が含まれていることを確認します（Target Membershipにチェックを入れる）。
5.  **権限の設定 (Info.plist)**:
    *   ファイルアクセスを行うため、特別な権限設定は通常不要ですが（ユーザー選択によるアクセス権付与のため）、バックグラウンドでの動作やファイル監視を行う場合は適切な設定が必要です。今回は基本的な読み書きのみです。

## 使い方

1.  アプリをiPhone（またはシミュレータ）で起動します。
2.  「フォルダを選択」ボタンをタップし、ObsidianのVault内のフォルダ（Tasknotesやデイリーノートがあるフォルダ）を選択します。
3.  **クイックメモ**: アプリ内のテキストボックスに入力し「保存」を押すと、その日の日付ファイル（例: `2023-10-27.md`）に `- [HH:mm] メモ` の形式で追記されます。
4.  **ウィジェット**: ホーム画面にウィジェットを追加すると、選択したフォルダ内の未完了タスクが表示されます。

## ソースコード構成

*   `Shared/`: アプリとウィジェットで共有するロジック
    *   `FolderManager.swift`: フォルダアクセスの管理、ブックマークの保存。
    *   `TaskParser.swift`: Markdownファイルの解析。
*   `App/`: メインアプリのUI
    *   `ContentView.swift`: 設定とメモ入力画面。
    *   `ObsidianApp.swift`: アプリのエントリポイント。
*   `Widget/`: ウィジェットの実装
    *   `TaskWidget.swift`: WidgetKitの実装。
