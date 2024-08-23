#!/bin/bash

# Check if the correct number of arguments is provided
if [ "$#" -ne 3 ]; then
    echo "Usage: $0 <key> <value> <gradle.properties path>"
    exit 1
fi

# Assign arguments to variables
key="$1"
value="$2"
gradle_properties_file="$3"

# Check if gradle.properties file exists
if [ ! -f "$gradle_properties_file" ]; then
    echo "gradle.properties file not found!"
    exit 1
fi

# Temporary file for writing updated properties
temp_file=$(mktemp)

# Flag to indicate if property was updated
updated=false

# Read each line from gradle.properties
while IFS= read -r line; do
    # Check if the line contains the key
    if [[ "$line" == "$key="* ]]; then
        # Update the value for the key
        updated=true
        echo "$key=$value" >> "$temp_file"
    else
        # Preserve other lines
        echo "$line" >> "$temp_file"
    fi
done < "$gradle_properties_file"

# If the property wasn't found, append it to the end of the file
if ! $updated; then
    echo "$key=$value" >> "$temp_file"
fi

# Replace the original file with the updated content
mv "$temp_file" "$gradle_properties_file"

echo "Updated $key in $gradle_properties_file to $value"
