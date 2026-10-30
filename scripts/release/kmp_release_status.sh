#!/bin/bash

# Reports where the KMP version in gradle.properties is in the release, so a
# failed publish can be re-run without redoing the steps that already happened.
# Emits version, tag, tagged and on_central (true/false) to $GITHUB_OUTPUT.
#
# Usage: $0

set -euo pipefail

GITHUB_OUTPUT="${GITHUB_OUTPUT:-/dev/stdout}"

VERSION=$(ruby scripts/versioning/util/get_gradle_property.rb kmpSdkVersionName)
if ! [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
  echo "❌ kmpSdkVersionName '$VERSION' isn't an x.y.z version"
  exit 1
fi
TAG="$VERSION-kmp"

TAGGED=false
if git ls-remote --exit-code --tags origin "refs/tags/$TAG" > /dev/null; then
  TAGGED=true
fi

POM="https://repo1.maven.org/maven2/com/twilio/twilio-verify-passkeys-common/$VERSION/twilio-verify-passkeys-common-$VERSION.pom"
ON_CENTRAL=false
if curl -fsI "$POM" > /dev/null; then
  ON_CENTRAL=true
fi

echo "KMP $VERSION: tagged=$TAGGED, on Maven Central=$ON_CENTRAL"
if [ "$ON_CENTRAL" == "true" ] && [ "$TAGGED" == "false" ]; then
  echo "❌ $VERSION is on Maven Central but has no $TAG tag. Tag the commit that was published, then re-run."
  exit 1
fi
if [ "$TAGGED" == "false" ]; then
  # Fails early if the version didn't come from a release PR.
  ./scripts/release/changelog_entry.sh "$VERSION" > /dev/null
fi

{
  echo "version=$VERSION"
  echo "tag=$TAG"
  echo "tagged=$TAGGED"
  echo "on_central=$ON_CENTRAL"
} >> "$GITHUB_OUTPUT"
