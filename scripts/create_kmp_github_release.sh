#!/bin/bash

# Create the KMP GitHub release for <version>-kmp, using the version's
# CHANGELOG.md entry as the notes. Skips a release that already exists.
# Requires GH_TOKEN.
#
# Usage: $0 <version>

set -euo pipefail

VERSION="${1:?Usage: $0 <version>}"
TAG="$VERSION-kmp"

if gh release view "$TAG" > /dev/null 2>&1; then
  echo "The $TAG release already exists"
  exit 0
fi

NOTES_FILE=$(mktemp)
./scripts/release/changelog_entry.sh "$VERSION" > "$NOTES_FILE"
gh release create "$TAG" --title "$TAG" --notes-file "$NOTES_FILE" --verify-tag
