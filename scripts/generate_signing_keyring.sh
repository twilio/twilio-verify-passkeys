#!/bin/bash

# Decode the base64 GPG signing key into the secret key ring file used by the
# Gradle signing plugin. Requires SIGNING_KEY and SIGNING_SECRET_KEY_RING_FILE.

: "${SIGNING_KEY:?Must set SIGNING_KEY}"
: "${SIGNING_SECRET_KEY_RING_FILE:?Must set SIGNING_SECRET_KEY_RING_FILE}"

echo "$SIGNING_KEY" | base64 -d >> "$SIGNING_SECRET_KEY_RING_FILE"
