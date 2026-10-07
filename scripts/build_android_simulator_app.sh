#!/bin/bash

# Build the Android sample app (debug APK) and package it for distribution.
#
# On GitHub Actions the downloadable URL is produced by the upload-artifact
# step in the workflow (its `artifact-url` output), so this script only builds
# and zips the app — it no longer constructs a CI-provider-specific URL.

set -euo pipefail

./gradlew :androidApp:assembleDebug
mv androidApp/build/outputs/apk/debug/androidApp-debug.apk sample-app.apk
zip -r sample-app.zip sample-app.apk

echo "Android sample app built: sample-app.apk / sample-app.zip"
