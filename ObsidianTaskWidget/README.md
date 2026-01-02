# Obsidian Tasknotes Widget for Android

このプロジェクトは、[Obsidian Tasknotesプラグイン](https://github.com/callumalpass/tasknotes)で管理されるタスクを表示するためのAndroidアプリウィジェットです。

## 機能

*   **フォルダ選択**: Obsidian Vault内のTasknotesが保存されている特定のフォルダを選択できます。
*   **YAML解析**: MarkdownファイルのYAMLフロントマターから `status`、`due`、`title` を読み取ります。
*   **ウィジェット**: 未完了のタスクをホーム画面上のスクロール可能なリストに表示します。
*   **フィルタリング**: `done` または `completed` とマークされたタスクを自動的に非表示にします。
*   **手動更新**: ウィジェット上のボタンでタスクを再読み込みできます。

## ビルドとインストール方法

これはソースコードリポジトリであるため、Android StudioまたはGradleを使用してビルドする必要があります。

### 前提条件

*   Android Studio
*   AndroidデバイスにObsidianがインストールされていること（ローカルファイルアクセスのため）
*   ObsidianでTasknotesプラグインが設定されていること

### 手順

1.  このプロジェクトフォルダ（`ObsidianTaskWidget`）をAndroid Studioで開きます。
2.  Gradleの同期が完了するのを待ちます。
3.  Androidデバイスを接続するか、エミュレータを起動します。
4.  アプリを実行します（`Run > Run 'app'`）。

## 使い方

1.  **権限の付与**: アプリを初めて開く際、「Select Task Folder」をタップします。
2.  **フォルダ選択**: Obsidian Vault > Tasknotesフォルダ（またはタスクを保存している場所）に移動し、「このフォルダを使用」をタップします。
3.  **ウィジェットの追加**: Androidのホーム画面に移動し、長押しして「ウィジェット」を選択、「Obsidian Task Widget」を見つけて画面にドラッグします。
4.  **タスクの表示**: ウィジェットにタスクが表示されます。Obsidianで変更を加えた場合は、更新アイコンを使用してリストを更新してください。

## 注意事項

*   このアプリは `OPEN_DOCUMENT_TREE` 権限を使用して、選択されたフォルダのみにアクセスします。Vault全体や他のストレージにはアクセスしません。
*   Tasknotesのデフォルトの動作に従い、タスクは個別のMarkdownファイル（1タスク1ノート）であることを前提としています。
*   ステータスが `done` または `completed` のタスクは除外されます。

## テストの実行

プロジェクトにはユニットテストが含まれています。Android Studioで `YamlParserTest` を右クリックして "Run 'YamlParserTest'" を選択するか、ターミナルで以下を実行してください：

```bash
./gradlew test
```
