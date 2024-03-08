#!/bin/bash

# Check if filename is provided as argument
if [ $# -ne 3 ]; then
    SELF=$(basename "$0")
    echo "Usage: $SELF <FIREBASE_APP_DISTRIBUTION_OUTPUT_FILE> <FOLDER_TO_SAVE_URL_AS_ENV> <ENV_FILE_NAME>"
    exit 1
fi

BINARY_DOWNLOAD_URI=$(grep -o '"binaryDownloadUri":"[^"]*' "$1" | grep -o '[^"]*$')

echo "Binary Download URI: $BINARY_DOWNLOAD_URI"
ANDROID_APP_DOWNLOAD_URL=$BINARY_DOWNLOAD_URI

FOLDER_TO_SAVE_URL=$2
FILE_NAME=$3
./scripts/add_env_variable_to_file.sh ANDROID_APP_DOWNLOAD_URL "$ANDROID_APP_DOWNLOAD_URL" "$FOLDER_TO_SAVE_URL" "$FILE_NAME"
