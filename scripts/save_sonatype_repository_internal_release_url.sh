#!/bin/bash

# Check if filename is provided as argument
if [ $# -ne 3 ]; then
    SELF=$(basename "$0")
    echo "Usage: $SELF <GRADLE_OUTPUT_FILE> <FOLDER_TO_SAVE_URL_AS_ENV> <ENV_FILE_NAME>"
    exit 1
fi

# Search for the string in the log file
REPO_OUTPUT=$(cat "$1" | grep 'Created staging repository')
if ! [[ "$REPO_OUTPUT" =~ "Created staging repository '"(.+)"'" ]]; then
    echo "Cannot parse staging repository name"
    exit 1
fi

REPO_NAME=${BASH_REMATCH[1]}
echo "$REPO_NAME"
REPO_URL=https://oss.sonatype.org/content/repositories/$REPO_NAME
echo "$REPO_URL"

FOLDER_TO_SAVE_URL=$2
FILE_NAME=$3
./scripts/add_env_variable_to_file.sh REPO_NAME "$REPO_NAME" "$FOLDER_TO_SAVE_URL" "$FILE_NAME"
./scripts/add_env_variable_to_file.sh REPO_URL "$REPO_URL" "$FOLDER_TO_SAVE_URL" "$FILE_NAME"
