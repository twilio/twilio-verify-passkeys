#!/bin/bash

# Promote the internal staging repository to Maven Central.
# Requires TMP_WORKSPACE and ENV_VARS_FILE (which provide REPO_NAME).

# shellcheck disable=SC1090
source "$TMP_WORKSPACE/$ENV_VARS_FILE"
echo "$REPO_NAME"
echo "Publishing to Maven Central"

./scripts/run_gradle_task.sh releaseSonatypeStagingRepository --staging-repository-id "$REPO_NAME"
