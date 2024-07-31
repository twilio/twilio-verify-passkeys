#!/bin/bash

set -e
set -x

if [ $# -ne 1 ]; then
    SELF=`basename $0`
    echo "Usage: $SELF <SDK_DIR>"
    exit 1
fi

SDK_DIR="$1"
LATEST_DIR="$SDK_DIR/cmdline-tools/latest"

if [ -d "$LATEST_DIR" ]; then
    echo "SDK download has been skipped: $LATEST_DIR already exists."
    exit 0
fi

curl https://dl.google.com/android/repository/commandlinetools-mac-7302050_latest.zip --output /tmp/android-commandlinetools.zip

rm -rf /tmp/cmdline-tools
unzip -q -o /tmp/android-commandlinetools.zip -d /tmp

mkdir -p "$LATEST_DIR"
mv /tmp/cmdline-tools/* "$LATEST_DIR"

yes | "$LATEST_DIR/bin/sdkmanager" --licenses
