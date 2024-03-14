#!/bin/bash

# Check if both key and value are provided
if [ $# -ne 4 ]; then
    echo "Usage: $0 <KEY> <VALUE> <FOLDER_PATH> <FILE_NAME>"
    exit 1
fi

# Assign parameters to variables
KEY=$1
VALUE=$2
FOLDER_PATH=$3
FILE_NAME=$4

mkdir -p "$FOLDER_PATH"

# Export the variable
echo "export $KEY=$VALUE" >> "$FOLDER_PATH"/"$FILE_NAME"
