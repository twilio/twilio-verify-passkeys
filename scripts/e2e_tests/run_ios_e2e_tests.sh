#!/bin/bash

# Bash script to trigger iOS internal SDK E2E tests

# Usage: ./run_ios_e2e_tests.sh <TMP_WORKSPACE> <ENV_VARS_FILE>

set -euo pipefail

# Set environment/workspace paths
TMP_WORKSPACE=$1
ENV_VARS_FILE=$2

# Load environment variables
source "$TMP_WORKSPACE/$ENV_VARS_FILE"

# Echo current value
echo "App URL: $IOS_SIMULATOR_APP_URL"

# Trigger the E2E tests pipeline
TRIGGERED_PIPELINE=$(./scripts/e2e_tests/trigger_e2e_tests_pipeline.sh "$IOS_SIMULATOR_APP_URL" "$CIRCLE_WORKFLOW_ID" "$WAITING_JOB_NAME" false true)

echo "Triggered pipeline: $TRIGGERED_PIPELINE"

# Persist the pipeline ID
./scripts/add_env_variable_to_file.sh IOS_INTERNAL_SDK_E2E_TEST_TRIGGERED_PIPELINE "$TRIGGERED_PIPELINE" "$TMP_WORKSPACE" "$ENV_VARS_FILE"