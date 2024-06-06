#!/bin/bash

if [ $# -ne 2 ]; then
    echo "Usage: $0 <TRIGGERED_PIPELINE> <WORKFLOW_NAME_TO_CHECK>"
    exit 1
fi

TRIGGERED_PIPELINE=$1
WORKFLOW_NAME_TO_CHECK=$2

# Fetch the workflow status for the triggered pipeline
TRIGGERED_PIPELINE_STATUS=$(curl --request GET \
  --url "https://circleci.com/api/v2/pipeline/${TRIGGERED_PIPELINE}/workflow" \
  --header "Circle-Token: $CIRCLE_TOKEN" \
  --header "Content-Type: application/json" | \
  jq -r --arg WORKFLOW_NAME "$WORKFLOW_NAME_TO_CHECK" \
  '.items[] | select(.name == $WORKFLOW_NAME) | .status')

if [ -z "$TRIGGERED_PIPELINE_STATUS" ]; then
    echo "Error: Workflow '$WORKFLOW_NAME_TO_CHECK' not found in pipeline '$TRIGGERED_PIPELINE'."
    exit 1
fi

echo "$TRIGGERED_PIPELINE_STATUS"
