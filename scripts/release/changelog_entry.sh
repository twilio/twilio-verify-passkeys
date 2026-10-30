#!/bin/bash

# Prints the body of the "# <version>" entry in CHANGELOG.md, without its title.
# Fails if there's no entry or it's empty.
#
# Usage: $0 <version>

set -euo pipefail

VERSION="${1:?Usage: $0 <version>}"

ENTRY=$(awk -v title="# $VERSION" '
  $0 == title { found = 1; next }
  found && /^# / { exit }
  found { print }
' CHANGELOG.md | sed '/./,$!d')

if [ -z "$ENTRY" ]; then
  echo "❌ CHANGELOG.md has no '# $VERSION' entry. Release PRs come from the Android Prepare Release workflow." >&2
  exit 1
fi
echo "$ENTRY"
