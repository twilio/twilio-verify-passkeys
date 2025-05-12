#!/bin/bash

# Bash script to trigger Android internal SDK E2E tests

# Usage: ./run_android_e2e_tests.sh <ANDROID_APP_URL> <WORKFLOW_ID> <WAITING_JOB_NAME>

set -euo pipefail

# Set environment/workspace paths
ANDROID_APP_URL=$1
WORKFLOW_ID=$2
WAITING_JOB_NAME=$3

echo "trigger-android"
echo "workflow-id: $WORKFLOW_ID"
echo "waiting-job-name: $WAITING_JOB_NAME"
echo "android_app_url: $ANDROID_APP_URL"