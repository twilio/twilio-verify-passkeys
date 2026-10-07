#!/bin/bash

# Build the iOS sample app for the simulator (via Fastlane) and package it.
#
# On GitHub Actions the downloadable URL is produced by the upload-artifact
# step in the workflow (its `artifact-url` output), so this script only builds
# and zips the app — it no longer constructs a CI-provider-specific URL.

set -euo pipefail

cd iosApp

# Build the simulator app using Fastlane.
bundle exec fastlane simulator_app

# Move and zip the resulting .app.
mv simulator_build/Build/Products/Debug-iphonesimulator/iosApp.app sample-app.app
zip -r sample-app.zip sample-app.app

echo "iOS sample app built: iosApp/sample-app.app / iosApp/sample-app.zip"
