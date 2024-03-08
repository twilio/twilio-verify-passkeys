#!/bin/bash

# Check if filename is provided as argument
if [ $# -ne 1 ]; then
    SELF=`basename $0`
    echo "Usage: $SELF <GRADLE_OUTPUT_FILE>"
    exit 1
fi

# Search for the string in the log file
REPO_OUTPUT=`cat "$1" | grep 'Created staging repository'`
if ! [[ "$REPO_OUTPUT" =~ "Created staging repository '"(.+)"'" ]]; then
    echo "Cannot parse staging repository name"
    exit 1
fi

REPO_NAME=${BASH_REMATCH[1]}
echo "$REPO_NAME"
REPO_URL=https://oss.sonatype.org/content/repositories/$REPO_NAME
echo "$REPO_URL"

TMP_FOLDER="tmp/workspace"
./scripts/add_env_variable_to_file.sh REPO_NAME "$REPO_NAME" $TMP_FOLDER
./scripts/add_env_variable_to_file.sh REPO_URL "$REPO_URL" $TMP_FOLDER
