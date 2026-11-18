#!/bin/bash

# Sets the author of the commits the release workflows make.
# Requires GITHUB_USER_NAME and GITHUB_USER_EMAIL.
#
# Usage: $0

set -euo pipefail

git config user.name "${GITHUB_USER_NAME:?Must set GITHUB_USER_NAME}"
git config user.email "${GITHUB_USER_EMAIL:?Must set GITHUB_USER_EMAIL}"
