#!/bin/bash

# Download the Android command line tools and point Gradle at them.
# Extracts the CircleCI `install-android-sdk` command.
#
# Usage: $0 [gradle_dir]   (gradle_dir defaults to the repo root ".")

GRADLE_DIR="${1:-.}"
ANDROID_SDK_ROOT="$HOME/android-sdk"

./scripts/download_android_sdk.sh "$ANDROID_SDK_ROOT"
echo "sdk.dir=$ANDROID_SDK_ROOT" >> "$GRADLE_DIR/local.properties"
echo "org.gradle.daemon=false" >> "$GRADLE_DIR/gradle.properties"
