#!/bin/bash

# Check if filename is provided as argument
if [ $# -ne 1 ]; then
    SELF=`basename $0`
    echo "Usage: $SELF <FIREBASE_APP_DISTRIBUTION_OUTPUT_FILE>"
    exit 1
fi

binary_download_uri=$(grep -o '"binaryDownloadUri":"[^"]*' "$1" | grep -o '[^"]*$')

echo "Binary Download URI: $binary_download_uri"
export ANDROID_APP_DOWNLOAD_URL=$binary_download_uri
echo "$ANDROID_APP_DOWNLOAD_URL"

TMP_FOLDER="tmp/workspace"
./scripts/add_env_variable_to_file.sh ANDROID_APP_DOWNLOAD_URL "$ANDROID_APP_DOWNLOAD_URL" $TMP_FOLDER
