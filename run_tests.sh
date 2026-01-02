#!/bin/bash
set -e

echo "=== Androidのテストを実行します ==="

echo "1. ObsidianTaskWidget のテストを実行中..."
cd ObsidianTaskWidget
# Android SDKがない環境ではgradlew testが失敗する可能性があるため、チェック
if [ -z "$ANDROID_HOME" ]; then
    echo "警告: ANDROID_HOMEが設定されていません。ビルドにはAndroid SDKが必要です。"
    echo "CI/CD環境またはローカル開発環境で './gradlew test' を実行してください。"
else
    ./gradlew test
fi
cd ..

echo "2. ObsidianMemoWidget のテストを実行中..."
cd ObsidianMemoWidget
if [ -z "$ANDROID_HOME" ]; then
     echo "警告: ANDROID_HOMEが設定されていません。"
else
    ./gradlew test
fi
cd ..

echo "=== iOSのテストについて ==="
echo "iOSのテストはXcode上で実行してください。"
echo "ObsidianiOSApp/Tests/TaskParserTests.swift が含まれています。"
