#!/bin/bash

# Uploads, signs and closes the KMP SDK in a Sonatype staging repository under a
# throwaway version, without releasing it, to check the signing key and
# credentials. Drop the staging repository in the Central Portal afterwards.
# Requires the same signing and Sonatype variables as publish_kmp_internal.sh.
#
# Usage: $0 <version>   (a pre-release version like 0.3.1-dryrun1)

set -eo pipefail

VERSION="${1:?Usage: $0 <version>}"
if ! [[ "$VERSION" =~ ^[0-9]+\.[0-9]+\.[0-9]+-[0-9A-Za-z.]+$ ]]; then
  echo "❌ '$VERSION' must be a pre-release version like 0.3.1-dryrun1, so it can't be mistaken for a release"
  exit 1
fi
POM="https://repo1.maven.org/maven2/com/twilio/twilio-verify-passkeys-common/$VERSION/twilio-verify-passkeys-common-$VERSION.pom"
if curl -fsI "$POM" > /dev/null; then
  echo "❌ $VERSION is already on Maven Central. Pick another dry-run version."
  exit 1
fi

./scripts/run_gradle_task.sh sonatypeTwilioPasskeysStagingRepositoryUpload -PkmpSdkVersionName="$VERSION" | tee gradle-task-output.log
REPO_NAME=$(sed -n "s/.*Created staging repository '\([^']*\)'.*/\1/p" gradle-task-output.log | head -n 1)

GITHUB_STEP_SUMMARY="${GITHUB_STEP_SUMMARY:-/dev/stdout}"
{
  echo "### Sonatype dry run: $VERSION"
  echo
  echo "Uploaded, signed and closed staging repository \`${REPO_NAME:-unknown}\`. It was not released."
  echo
  echo "Drop it under Deployments at https://central.sonatype.com/publishing/deployments."
} >> "$GITHUB_STEP_SUMMARY"
