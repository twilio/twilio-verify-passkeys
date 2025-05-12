#!/bin/bash

# Bash script to check Android internal SDK E2E tests result

# Usage: ./check_android_e2e_result.sh <TMP_WORKSPACE> <ENV_VARS_FILE> <WORKFLOW_NAME_TO_CHECK>

set -euo pipefail

# Set environment/workspace paths
TMP_WORKSPACE=$1
ENV_VARS_FILE=$2
WORKFLOW_NAME_TO_CHECK=$3
# Load environment variables
source "$TMP_WORKSPACE/$ENV_VARS_FILE"

# Echo current value
echo "Pipeline: $ANDROID_LOCAL_E2E_TESTS_TRIGGERED_PIPELINE"

# Check the E2E tests result
TRIGGERED_E2E_TESTS_PIPELINE_STATUS=$(./scripts/e2e_tests/check_triggered_e2e_tests_pipeline_status.sh $ANDROID_LOCAL_E2E_TESTS_TRIGGERED_PIPELINE $WORKFLOW_NAME_TO_CHECK)
echo "Triggered pipeline status: $TRIGGERED_E2E_TESTS_PIPELINE_STATUS"
            
if [[ "$TRIGGERED_E2E_TESTS_PIPELINE_STATUS" != "success"* ]]; then
    echo "Workflow not successful - ${TRIGGERED_E2E_TESTS_PIPELINE_STATUS}"
    (exit -1) 
fi