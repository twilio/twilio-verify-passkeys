#!/bin/bash

# Bash script to build iOS simulator app and prepare artifact URL

set -euo pipefail

# Ensure environment variables are set
: "${CIRCLE_WORKFLOW_JOB_ID:?Must set CIRCLE_WORKFLOW_JOB_ID}"
: "${CIRCLE_NODE_INDEX:?Must set CIRCLE_NODE_INDEX}"
: "${TMP_WORKSPACE:?Must set TMP_WORKSPACE}"
: "${ENV_VARS_FILE:?Must set ENV_VARS_FILE}"

cd iosApp

# Build the simulator app using Fastlane
bundle exec fastlane simulator_app

# Move and zip the resulting .app
mv simulator_build/Build/Products/Debug-iphonesimulator/iosApp.app sample-app.app
zip -r sample-app.zip sample-app.app

# Export the app URL as environment variable (used in CircleCI)
IOS_SIMULATOR_APP_URL="https://output.circle-artifacts.com/output/job/${CIRCLE_WORKFLOW_JOB_ID}/artifacts/${CIRCLE_NODE_INDEX}/iosApp/sample-app.zip"
export IOS_SIMULATOR_APP_URL

echo "Generated iOS Simulator App URL: $IOS_SIMULATOR_APP_URL"

cd ..

# Persist to environment file
./scripts/add_env_variable_to_file.sh IOS_SIMULATOR_APP_URL "$IOS_SIMULATOR_APP_URL" "$TMP_WORKSPACE" "$ENV_VARS_FILE"
