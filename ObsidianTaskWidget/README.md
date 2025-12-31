# Obsidian Tasknotes Widget for Android

This project is an Android App Widget designed to display tasks managed by the [Obsidian Tasknotes plugin](https://github.com/callumalpass/tasknotes).

## Features

*   **Custom Folder Selection**: Select the specific folder in your Obsidian vault where Tasknotes are stored.
*   **YAML Parsing**: Reads `status`, `due`, and `title` from the YAML frontmatter of Markdown files.
*   **Widget**: A scrollable list on the home screen showing open tasks.
*   **Filtering**: Automatically hides tasks marked as `done` or `completed`.
*   **Manual Refresh**: Button on the widget to reload tasks.

## How to Build and Install

Since this is a source code repository, you need to build it using Android Studio or Gradle.

### Prerequisites

*   Android Studio
*   Obsidian installed on your Android device (for local file access)
*   Tasknotes plugin configured in Obsidian

### Steps

1.  Open this project folder (`ObsidianTaskWidget`) in Android Studio.
2.  Wait for Gradle to sync.
3.  Connect your Android device or start an emulator.
4.  Run the app (`Run > Run 'app'`).

## Usage

1.  **Grant Permission**: When you first open the app, tap "Select Task Folder".
2.  **Select Folder**: Navigate to your Obsidian Vault > Tasknotes folder (or wherever you store your tasks) and tap "Use this folder".
3.  **Add Widget**: Go to your Android home screen, long press, select "Widgets", find "Obsidian Task Widget", and drag it to your screen.
4.  **View Tasks**: The widget should populate with your tasks. Use the refresh icon to update the list if you make changes in Obsidian.

## Notes

*   The app uses the `OPEN_DOCUMENT_TREE` permission to access only the folder you select. It does not access your entire vault or other storage.
*   It assumes tasks are individual Markdown files (one note per task) as per Tasknotes default behavior.
*   It filters out tasks with status: `done` or `completed`.
