#!/bin/bash

# Bash script to trigger Android internal SDK E2E tests

# Usage: ./run_android_e2e_tests.sh <TMP_WORKSPACE> <ENV_VARS_FILE> <WAITING_JOB_NAME>

set -euo pipefail

# Set environment/workspace paths
TMP_WORKSPACE=$1
ENV_VARS_FILE=$2
WAITING_JOB_NAME=$3
# Load environment variables
source "$TMP_WORKSPACE/$ENV_VARS_FILE"

# Echo current value
echo "App URL: $ANDROID_APP_DOWNLOAD_URL"

# Trigger the E2E tests pipeline
TRIGGERED_PIPELINE=$(./scripts/e2e_tests/trigger_e2e_tests_pipeline.sh "$ANDROID_APP_DOWNLOAD_URL" "$CIRCLE_WORKFLOW_ID" "$WAITING_JOB_NAME" true false)

echo "Triggered pipeline: $TRIGGERED_PIPELINE"

# Persist the pipeline ID
./scripts/add_env_variable_to_file.sh ANDROID_LOCAL_E2E_TESTS_TRIGGERED_PIPELINE "$TRIGGERED_PIPELINE" "$TMP_WORKSPACE" "$ENV_VARS_FILE"