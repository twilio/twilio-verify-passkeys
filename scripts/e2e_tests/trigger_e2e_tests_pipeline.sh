#!/bin/bash

if [ $# -ne 5 ]; then
    echo "Usage: $0 <APP_DOWNLOAD_URL> <TRIGGERING_WORKFLOW_ID> <TRIGGERING_WORKFLOW_WAITING_JOB_NAME> <TRIGGER_ANDROID_WORKFLOW> <TRIGGER_IOS_WORKFLOW>"
    exit 1
fi

APP_DOWNLOAD_URL=$1
TRIGGERING_WORKFLOW_ID=$2
TRIGGERING_WORKFLOW_WAITING_JOB_NAME=$3
TRIGGER_ANDROID_WORKFLOW=$4
TRIGGER_IOS_WORKFLOW=$5

DATA_PAYLOAD=$(jq -n \
--arg trigger_android_workflow "$TRIGGER_ANDROID_WORKFLOW" \
--arg trigger_ios_workflow "$TRIGGER_IOS_WORKFLOW" \
--arg triggering_workflow_id "$TRIGGERING_WORKFLOW_ID" \
--arg triggering_workflow_waiting_job_name "$TRIGGERING_WORKFLOW_WAITING_JOB_NAME" \
'{
    "branch": "main",
    "parameters": {
        "trigger-android-workflow": ($trigger_android_workflow | fromjson),
        "trigger-ios-workflow": ($trigger_ios_workflow | fromjson),
        "triggering-workflow-id": $triggering_workflow_id,
        "triggering-workflow-waiting-job-name": $triggering_workflow_waiting_job_name
    }
}')

if [ "$TRIGGER_IOS_WORKFLOW" == "true" ]; then
    DATA_PAYLOAD=$(echo "$DATA_PAYLOAD" | jq --arg ios_app_url "$APP_DOWNLOAD_URL" '.parameters["ios-simulator-app-url"]=$ios_app_url')
else
    DATA_PAYLOAD=$(echo "$DATA_PAYLOAD" | jq --arg android_app_url "$APP_DOWNLOAD_URL" '.parameters["android-app-url"]=$android_app_url')
fi

TRIGGERED_PIPELINE=$(curl --request POST \
  --url https://circleci.com/api/v2/project/gh/twilio/twilio-verify-passkeys-tests/pipeline \
  --header "Circle-Token: $CIRCLE_TOKEN" \
  --header "Content-Type: application/json" \
  --data "$DATA_PAYLOAD" | jq -r '.id')

# Check if the pipeline creation was successful
if [ -z "$TRIGGERED_PIPELINE" ]; then
    echo "Failed to create pipeline"
    exit 1
fi

echo "$TRIGGERED_PIPELINE"
