#!/bin/bash

# Bash script to release iOS SDK by publishing the XCFramework to GitHub

set -euo pipefail

# Ensure required env vars are set
: "${NEW_IOS_SDK_VERSION:?Must set NEW_IOS_SDK_VERSION}"
: "${GITHUB_USER_EMAIL:?Must set GITHUB_USER_EMAIL}"
: "${GITHUB_USER_NAME:?Must set GITHUB_USER_NAME}"
: "${GITHUB_API_TOKEN:?Must set GITHUB_API_TOKEN}"

# Check for release zip
if [ ! -f releaseFramework.zip ]; then
  echo "releaseFramework.zip not found in workspace"
  exit 1
fi

# Rename zip
mv releaseFramework.zip TwilioPasskeysAuthentication.xcframework.zip

# Generate checksum
CHECKSUM=$(sha256sum TwilioPasskeysAuthentication.xcframework.zip | awk '{print $1}')
echo "Checksum: $CHECKSUM"

# Generate changelog
CHANGELOG=$(ruby scripts/versioning/generate_changelog.rb true)
echo "Changelog: $CHANGELOG"

# Clone the repo
git clone git@github.com:twilio/twilio-verify-passkeys-ios.git
cd twilio-verify-passkeys-ios

# Update Package.swift with new URL and checksum
sed -i "s|url: .*|url: \"https://github.com/twilio/twilio-verify-passkeys-ios/releases/download/v$NEW_IOS_SDK_VERSION/TwilioPasskeysAuthentication.xcframework.zip\",|" Package.swift
sed -i "s|checksum:\".*\"|checksum: \"$CHECKSUM\"|" Package.swift

# Commit and tag
git config user.email "$GITHUB_USER_EMAIL"
git config user.name "$GITHUB_USER_NAME"
git add .
git commit -m "Update to version v$NEW_IOS_SDK_VERSION"
git tag "v$NEW_IOS_SDK_VERSION"
git push origin main
git push origin "v$NEW_IOS_SDK_VERSION"

# Install dependency and create GitHub release
gem install faraday
ruby ../scripts/versioning/create_github_release.rb \
  twilio \
  twilio-verify-passkeys-ios \
  "$GITHUB_API_TOKEN" \
  "v$NEW_IOS_SDK_VERSION" \
  "TwilioPasskeysAuthentication v$NEW_IOS_SDK_VERSION" \
  "${CHANGELOG}" \
  ../TwilioPasskeysAuthentication.xcframework.zip
