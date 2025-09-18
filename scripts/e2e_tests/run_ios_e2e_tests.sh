#!/bin/bash

# Bash script to trigger iOS internal SDK E2E tests

# Usage: ./run_ios_e2e_tests.sh <IOS_APP_URL> <WORKFLOW_ID> <WAITING_JOB_NAME>

set -euo pipefail

# Set environment/workspace paths
IOS_APP_URL=$1
WORKFLOW_ID=$2
WAITING_JOB_NAME=$3

echo "trigger-ios"
echo "workflow-id: $WORKFLOW_ID"
echo "waiting-job-name: $WAITING_JOB_NAME"
echo "ios_app_url: $IOS_APP_URL"