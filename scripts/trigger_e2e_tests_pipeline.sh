#!/bin/bash

if [ $# -ne 5 ]; then
    echo "Usage: $0 <APP_DOWNLOAD_URL> <TRIGGERING_PIPELINE_ID> <TRIGGERING_PIPELINE_WAITING_JOB_NAME> <TRIGGER_ANDROID_WORKFLOW> <TRIGGER_IOS_TRIGGER_ANDROID_WORKFLOW>"
    exit 1
fi

ANDROID_APP_DOWNLOAD_URL=$1
TRIGGERING_PIPELINE_ID=$2
TRIGGERING_PIPELINE_WAITING_JOB_NAME=$3
TRIGGER_ANDROID_WORKFLOW=$4
TRIGGER_IOS_WORKFLOW=$5

TRIGGERED_PIPELINE=$(curl --request POST \
  --url https://circleci.com/api/v2/project/gh/twilio/twilio-verify-passkeys-tests/pipeline \
  --header "Circle-Token: $CIRCLE_TOKEN" \
  --header "Content-Type: application/json" \
  --data '{
    "branch":"appium-main",
    "parameters":{
      "android-app-url":"'"$ANDROID_APP_DOWNLOAD_URL"'",
      "trigger-android-workflow":'"$TRIGGER_ANDROID_WORKFLOW"',
      "trigger-ios-workflow":'"$TRIGGER_IOS_WORKFLOW"'
      "triggering-pipeline-id":"'"$TRIGGERING_PIPELINE_ID"'",
      "triggering-pipeline-waiting-job-name":"'"$TRIGGERING_PIPELINE_WAITING_JOB_NAME"'"
    }
  }' | jq -r '.id')

# Check if the pipeline creation was successful
if [ -z "$TRIGGERED_PIPELINE" ]; then
    echo "Failed to create pipeline"
    exit 1
fi

echo "$TRIGGERED_PIPELINE"
