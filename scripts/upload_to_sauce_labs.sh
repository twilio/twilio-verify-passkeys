#!/bin/bash

# Upload a sample app (Android APK or zipped iOS simulator .app) to Sauce Labs app storage.
#
# Usage: ./upload_to_sauce_labs.sh [APK_PATH] [APP_NAME]
# Needs curl and jq. On GitHub Actions the app id is also written to $GITHUB_OUTPUT as app_id.
#
# Requires SAUCE_USERNAME and SAUCE_ACCESS_KEY. SAUCE_REGION is optional
# (us-west-1 by default; eu-central-1 and us-east-4 are also valid).

set -euo pipefail

APK_PATH="${1:-sample-app.apk}"
APP_NAME="${2:-sample-app.apk}"
SAUCE_REGION="${SAUCE_REGION:-us-west-1}"

: "${SAUCE_USERNAME:?SAUCE_USERNAME is not set}"
: "${SAUCE_ACCESS_KEY:?SAUCE_ACCESS_KEY is not set}"

if [ ! -f "$APK_PATH" ]; then
  echo "APK not found: $APK_PATH"
  exit 1
fi

RESPONSE_FILE="sauce_labs_upload_response.json"

HTTP_CODE=$(curl --silent --show-error --output "$RESPONSE_FILE" --write-out '%{http_code}' \
  --user "${SAUCE_USERNAME}:${SAUCE_ACCESS_KEY}" \
  --location "https://api.${SAUCE_REGION}.saucelabs.com/v1/storage/upload" \
  --form "payload=@${APK_PATH}" \
  --form "name=${APP_NAME}")

cat "$RESPONSE_FILE"
echo

if [ "$HTTP_CODE" -lt 200 ] || [ "$HTTP_CODE" -ge 300 ]; then
  echo "Sauce Labs upload failed (HTTP $HTTP_CODE)"
  exit 1
fi

SAUCE_APP_ID=$(jq -r '.item.id' "$RESPONSE_FILE")
if [ -z "$SAUCE_APP_ID" ] || [ "$SAUCE_APP_ID" == "null" ]; then
  echo "Could not read the app id from the Sauce Labs response"
  exit 1
fi

echo "Uploaded $APK_PATH to Sauce Labs as $APP_NAME (storage:filename=$APP_NAME)"
echo "Sauce Labs app id: $SAUCE_APP_ID (storage:$SAUCE_APP_ID)"

# Expose the id to later steps/jobs when running on GitHub Actions.
if [ -n "${GITHUB_OUTPUT:-}" ]; then
  echo "app_id=$SAUCE_APP_ID" >> "$GITHUB_OUTPUT"
fi
