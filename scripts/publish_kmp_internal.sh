#!/bin/bash

# Publish the KMP SDK to the internal Sonatype staging repository and save the
# resulting repository name/URL to the env file for the Maven Central step.
# Requires TMP_WORKSPACE and ENV_VARS_FILE.

./scripts/run_gradle_task.sh sonatypeTwilioPasskeysStagingRepositoryUpload | tee gradle-task-output.log
./scripts/save_sonatype_repository_internal_release_url.sh gradle-task-output.log "$TMP_WORKSPACE" "$ENV_VARS_FILE"
