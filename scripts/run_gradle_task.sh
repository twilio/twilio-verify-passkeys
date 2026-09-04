#!/bin/bash

TASK="${1:?Usage: $0 <task> [args...]}"

./gradlew "$@"
