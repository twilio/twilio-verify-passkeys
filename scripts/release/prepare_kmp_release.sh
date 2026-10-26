#!/bin/bash

# Prepares the KMP release PR. Works out the release type from the conventional
# commits since the last -kmp tag, bumps kmpSdkVersionName in gradle.properties,
# adds the release's CHANGELOG.md entry and commits both on release/<version>-kmp.
# Emits branch (empty when there's nothing to release), version and title to
# $GITHUB_OUTPUT, and writes the release notes to build/release-notes.md.
#
# Usage: $0

set -euo pipefail

GITHUB_OUTPUT="${GITHUB_OUTPUT:-/dev/stdout}"

RELEASE_TYPE=$(ruby scripts/versioning/require_release.rb false)
case "$RELEASE_TYPE" in
  NONE)
    echo "No need to release a new KMP version"
    echo "branch=" >> "$GITHUB_OUTPUT"
    exit 0
    ;;
  MAJOR | MINOR | PATCH) ;;
  *)
    echo "❌ require_release.rb returned: $RELEASE_TYPE"
    exit 1
    ;;
esac

VERSION=$(ruby scripts/versioning/bump_kmp_sdk_version.rb "$RELEASE_TYPE" kmpSdkVersionName)
TAG="$VERSION-kmp"
BRANCH="release/$TAG"
echo "$RELEASE_TYPE release: $TAG"

if git ls-remote --exit-code --tags origin "refs/tags/$TAG" > /dev/null; then
  echo "❌ $TAG is already tagged. Check kmpSdkVersionName in gradle.properties."
  exit 1
fi
if [ -n "$(git ls-remote --heads origin "$BRANCH")" ]; then
  echo "❌ $BRANCH already exists. Merge or close its PR before preparing the release again."
  exit 1
fi

# The generator titles the entry "# <version>", which is how the publish
# workflow finds the release notes.
mkdir -p build
ruby scripts/versioning/generate_changelog.rb false > build/release-notes.md
{
  head -n 1 CHANGELOG.md
  echo
  cat build/release-notes.md
  echo
  tail -n +2 CHANGELOG.md | sed '/./,$!d'
} > build/CHANGELOG.md
mv build/CHANGELOG.md CHANGELOG.md

git switch -c "$BRANCH"
git add gradle.properties CHANGELOG.md
git commit -m "chore(release): $TAG"

{
  echo "branch=$BRANCH"
  echo "version=$VERSION"
  echo "title=Release $TAG"
} >> "$GITHUB_OUTPUT"
