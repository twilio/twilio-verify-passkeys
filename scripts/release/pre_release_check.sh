#!/bin/bash

# Pre-release check shared by both platforms. Identifies the release type, bumps
# the version and verifies it is not identical to the last released version.
# Emits should_release (and new_version) to $GITHUB_OUTPUT, replacing the
# CircleCI `circleci-agent step halt` behaviour.
#
# Usage: $0 <kmp|ios>

PLATFORM="${1:?Usage: $0 <kmp|ios>}"

if [ "$PLATFORM" == "kmp" ]; then
  IS_IOS="false"
  VERSION_PROPERTY="kmpSdkVersionName"
  PLATFORM_KEY="KMP"
  LABEL="KMP"
elif [ "$PLATFORM" == "ios" ]; then
  IS_IOS="true"
  VERSION_PROPERTY="iosSdkVersionName"
  PLATFORM_KEY="IOS"
  LABEL="iOS"
else
  echo "Unknown platform: $PLATFORM. Use 'kmp' or 'ios'."
  exit 1
fi

GITHUB_OUTPUT="${GITHUB_OUTPUT:-/dev/stdout}"

# Identify the type of release (major, minor, patch, or none).
RELEASE_TYPE=$(ruby scripts/versioning/require_release.rb "$IS_IOS")
echo "Release type = $RELEASE_TYPE"

if [ "$RELEASE_TYPE" == "NONE" ]; then
  echo "No need to release a new version"
  echo "should_release=false" >> "$GITHUB_OUTPUT"
  exit 0
fi

echo "Starting a precheck for a $RELEASE_TYPE release"

# Bump the SDK version based on the release type.
NEW_SDK_VERSION=$(ruby scripts/versioning/bump_kmp_sdk_version.rb "$RELEASE_TYPE" "$VERSION_PROPERTY")
echo "Version Bump = $NEW_SDK_VERSION"

# Fetch the latest released SDK version.
LATEST_SDK_VERSION=$(ruby scripts/versioning/git/get_last_released_version.rb "$PLATFORM_KEY")
echo "Latest $LABEL SDK version = $LATEST_SDK_VERSION"

# Compare versions and abort if identical.
if [ "$NEW_SDK_VERSION" = "$LATEST_SDK_VERSION" ]; then
  echo "❌ Release Aborted: The new $LABEL SDK version $NEW_SDK_VERSION is identical to the latest released version ($LATEST_SDK_VERSION)."
  exit 1
fi

echo "✅ New $LABEL SDK version $NEW_SDK_VERSION is unique and ready for release."
echo "should_release=true" >> "$GITHUB_OUTPUT"
echo "new_version=$NEW_SDK_VERSION" >> "$GITHUB_OUTPUT"
