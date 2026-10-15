#!/bin/bash

FOLDER="${1:?Usage: $0 <folder>}"

mkdir -p "$FOLDER"
find . -type f -regex ".*/build/test-results/.*xml" -exec cp {} "$FOLDER" \;
