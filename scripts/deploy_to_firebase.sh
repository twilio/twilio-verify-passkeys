#!/bin/bash

PLATFORM="${1:?Usage: $0 <android|ios>}"

if [ "$PLATFORM" == "android" ]; then
  # Use the latest commit message as the release notes (matches the iOS deploy lane).
  COMMIT_MESSAGE=$(git log -1 --pretty=%B)
  firebase appdistribution:distribute sample-app.apk --app "$FIREBASE_ANDROID_APP_ID" --token "$FIREBASE_TOKEN" --groups qa --debug --release-notes "$COMMIT_MESSAGE" | tee firebase_app_distribution_output.log
elif [ "$PLATFORM" == "ios" ]; then
  cd iosApp || exit 1
  bundle exec fastlane deploy_demo_app
else
  echo "Unknown platform: $PLATFORM. Use 'android' or 'ios'."
  exit 1
fi
