#!/bin/bash

# Check if both key and value are provided
if [ $# -ne 3 ]; then
    echo "Usage: $0 <key> <value> <folder>"
    exit 1
fi

# Assign parameters to variables
KEY=$1
VALUE=$2
TMP_FOLDER=$3

mkdir -p "$TMP_FOLDER"

# Export the variable
echo "export ""$KEY""=""$VALUE""" >> "${TMP_FOLDER}"/env_vars
