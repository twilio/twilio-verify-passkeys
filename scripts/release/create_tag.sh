#!/bin/bash

# Tags the current commit and pushes the tag.
#
# Usage: $0 <tag>

set -euo pipefail

TAG="${1:?Usage: $0 <tag>}"

git tag "$TAG" "$(git rev-parse HEAD)"
git push origin "refs/tags/$TAG"
