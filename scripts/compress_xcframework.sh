#!/bin/bash

# Bash script to compress debug and release XCFrameworks

set -euo pipefail

FRAMEWORK_NAME="TwilioPasskeysAuthentication.xcframework"

echo "Zipping debug XCFramework..."
ln -s "shared/build/XCFrameworks/debug/$FRAMEWORK_NAME" "$FRAMEWORK_NAME"
zip -r debugFramework.zip "$FRAMEWORK_NAME"
rm "$FRAMEWORK_NAME"

echo "Zipping release XCFramework..."
ln -s "shared/build/XCFrameworks/release/$FRAMEWORK_NAME" "$FRAMEWORK_NAME"
zip -r releaseFramework.zip "$FRAMEWORK_NAME"
rm "$FRAMEWORK_NAME"

echo "Compression complete: debugFramework.zip and releaseFramework.zip created."
