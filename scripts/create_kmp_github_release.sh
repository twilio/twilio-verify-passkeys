#!/bin/bash

# Create the KMP GitHub release for the given tag, using the generated changelog.
# Requires GITHUB_API_TOKEN.
#
# Usage: $0 <tag_version>

TAG_VERSION="${1:?Usage: $0 <tag_version>}"

RELEASE_NOTES=$(ruby scripts/versioning/generate_changelog.rb false)
ruby scripts/versioning/create_github_release.rb twilio twilio-verify-passkeys "$GITHUB_API_TOKEN" "$TAG_VERSION" "$TAG_VERSION" "${RELEASE_NOTES}"
