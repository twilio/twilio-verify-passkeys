#!/bin/bash

# Pushes the release branch and opens its PR into main. Requires GH_TOKEN, with
# write access to the repo.
#
# Usage: $0 <branch> <title> <body_file>

set -euo pipefail

BRANCH="${1:?Usage: $0 <branch> <title> <body_file>}"
TITLE="${2:?Usage: $0 <branch> <title> <body_file>}"
BODY_FILE="${3:?Usage: $0 <branch> <title> <body_file>}"

gh auth setup-git
git push origin "$BRANCH"
gh pr create --base main --head "$BRANCH" --title "$TITLE" --body-file "$BODY_FILE"
