#!/bin/bash

# Ensure the `apkanalyzer` tool is available on PATH. It ships with the Android
# SDK command line tools and is used by the :shared:measureSize Gradle task.
# On bare/self-hosted runners the SDK may be present (builds succeed) while
# cmdline-tools/bin is not on PATH, so this locates or installs it and exports
# the directory to $GITHUB_PATH for subsequent steps.

set -euo pipefail

if command -v apkanalyzer >/dev/null 2>&1; then
  echo "apkanalyzer already on PATH: $(command -v apkanalyzer)"
  exit 0
fi

SDK_ROOT="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$HOME/android-sdk}}"
echo "Looking for apkanalyzer under ${SDK_ROOT} ..."

BIN_DIR=""
for dir in "$SDK_ROOT/cmdline-tools/latest/bin" "$SDK_ROOT"/cmdline-tools/*/bin "$SDK_ROOT/tools/bin"; do
  if [ -x "$dir/apkanalyzer" ]; then
    BIN_DIR="$dir"
    break
  fi
done

if [ -z "$BIN_DIR" ]; then
  echo "apkanalyzer not found; installing cmdline-tools via sdkmanager..."
  SDKMANAGER=""
  for sm in "$SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" "$SDK_ROOT"/cmdline-tools/*/bin/sdkmanager; do
    if [ -x "$sm" ]; then
      SDKMANAGER="$sm"
      break
    fi
  done
  : "${SDKMANAGER:?Could not find sdkmanager under ${SDK_ROOT}; provision the Android SDK cmdline-tools on the runner}"
  yes | "$SDKMANAGER" "cmdline-tools;latest"
  BIN_DIR="$SDK_ROOT/cmdline-tools/latest/bin"
fi

echo "Adding ${BIN_DIR} to PATH"
echo "$BIN_DIR" >> "${GITHUB_PATH:-/dev/stdout}"
"$BIN_DIR/apkanalyzer" --version || true
