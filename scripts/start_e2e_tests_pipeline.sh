#!/bin/bash

PLATFORM="${1:?Usage: $0 <android|ios> <app_url> <workflow_id> <waiting_job_name>}"
APP_URL="${2:?Usage: $0 <android|ios> <app_url> <workflow_id> <waiting_job_name>}"
WORKFLOW_ID="${3:?Usage: $0 <android|ios> <app_url> <workflow_id> <waiting_job_name>}"
WAITING_JOB_NAME="${4:?Usage: $0 <android|ios> <app_url> <workflow_id> <waiting_job_name>}"

if [ "$PLATFORM" == "android" ]; then
  TRIGGER_PIPELINE=$(./scripts/e2e_tests/run_android_e2e_tests.sh $APP_URL $WORKFLOW_ID $WAITING_JOB_NAME)
elif [ "$PLATFORM" == "ios" ]; then
  TRIGGER_PIPELINE=$(./scripts/e2e_tests/run_ios_e2e_tests.sh $APP_URL $WORKFLOW_ID $WAITING_JOB_NAME)
else
  echo "Unknown platform: $PLATFORM. Use 'android' or 'ios'."
  exit 1
fi

echo "Trigger pipeline: $TRIGGER_PIPELINE"
