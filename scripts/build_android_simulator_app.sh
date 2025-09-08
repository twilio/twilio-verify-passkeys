#!/bin/bash

# Bash script to build Android simulator app and prepare artifact URL

set -euo pipefail

# Ensure environment variables are set
: "${CIRCLE_WORKFLOW_JOB_ID:?Must set CIRCLE_WORKFLOW_JOB_ID}"
: "${CIRCLE_NODE_INDEX:?Must set CIRCLE_NODE_INDEX}"
: "${TMP_WORKSPACE:?Must set TMP_WORKSPACE}"
: "${ENV_VARS_FILE:?Must set ENV_VARS_FILE}"

./gradlew :androidApp:assembleDebug
mv androidApp/build/outputs/apk/debug/androidApp-debug.apk sample-app.apk
zip -r sample-app.zip sample-app.apk

# Export the app URL as environment variable (used in CircleCI)
ANDROID_APP_DOWNLOAD_URL="https://output.circle-artifacts.com/output/job/${CIRCLE_WORKFLOW_JOB_ID}/artifacts/${CIRCLE_NODE_INDEX}/sample-app.zip"
export ANDROID_APP_DOWNLOAD_URL

echo "Generated Android Simulator App URL: $ANDROID_APP_DOWNLOAD_URL"

# Persist to environment file
./scripts/add_env_variable_to_file.sh ANDROID_APP_DOWNLOAD_URL "$ANDROID_APP_DOWNLOAD_URL" "$TMP_WORKSPACE" "$ENV_VARS_FILE"
