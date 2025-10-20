#!/bin/bash

# Bash script to release iOS SDK by publishing the XCFramework to GitHub

set -euo pipefail

# Determine iOS SDK version
echo "Determining iOS SDK version..."
IOS_RELEASE_TYPE=$(ruby scripts/versioning/require_release.rb true)
echo "Release type = $IOS_RELEASE_TYPE"

if [ "$IOS_RELEASE_TYPE" == "NONE" ]; then
  echo "No need to release a new version"
  exit 0
fi

NEW_IOS_SDK_VERSION=$(ruby scripts/versioning/bump_kmp_sdk_version.rb "$IOS_RELEASE_TYPE" iosSdkVersionName)
echo "iOS SDK version = $NEW_IOS_SDK_VERSION"

# Verify this version doesn't already exist
LATEST_IOS_SDK_VERSION=$(ruby scripts/versioning/git/get_last_released_version.rb IOS)
echo "Latest iOS SDK version = $LATEST_IOS_SDK_VERSION"

if [ "$NEW_IOS_SDK_VERSION" = "$LATEST_IOS_SDK_VERSION" ]; then
  echo "❌ Release Aborted: The new iOS SDK version $NEW_IOS_SDK_VERSION is identical to the latest released version ($LATEST_IOS_SDK_VERSION)."
  echo "Error: Version $NEW_IOS_SDK_VERSION already exists as the latest release ($LATEST_IOS_SDK_VERSION)."
  exit 1
fi

echo "✅ New iOS SDK version $NEW_IOS_SDK_VERSION is unique and ready for release."

# Ensure other required env vars are set
: "${GITHUB_USER_EMAIL:?Must set GITHUB_USER_EMAIL}"
: "${GITHUB_USER_NAME:?Must set GITHUB_USER_NAME}"
: "${GITHUB_API_TOKEN:?Must set GITHUB_API_TOKEN}"

echo "Releasing iOS SDK version $NEW_IOS_SDK_VERSION"

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
