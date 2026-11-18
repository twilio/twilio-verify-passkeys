#!/bin/bash

FROM="${1:?Usage: $0 <from> <to>}"
TO="${2:?Usage: $0 <from> <to>}"

cp -R "$FROM" "$TO"
