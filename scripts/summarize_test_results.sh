#!/bin/bash

# Aggregates JUnit XML test results into a markdown summary that is appended to
# the GitHub Actions run summary ($GITHUB_STEP_SUMMARY). Used by the Android and
# iOS unit-test jobs so results are visible without downloading the artifact.
#
# Usage: summarize_test_results.sh <title> <junit_results_dir>

set -euo pipefail

TITLE="${1:?Usage: $0 <title> <junit_results_dir>}"
RESULTS_DIR="${2:?Usage: $0 <title> <junit_results_dir>}"

# When run outside GitHub Actions, fall back to stdout so the script stays usable
# locally.
SUMMARY_FILE="${GITHUB_STEP_SUMMARY:-/dev/stdout}"

if [ ! -d "$RESULTS_DIR" ] || [ -z "$(find "$RESULTS_DIR" -name '*.xml' -print -quit)" ]; then
  {
    echo "### ${TITLE}"
    echo ""
    echo "> No JUnit test results were found."
    echo ""
  } >> "$SUMMARY_FILE"
  exit 0
fi

# Sum the attributes across every <testsuite> element in every XML file.
read -r TOTAL FAILURES ERRORS SKIPPED TIME <<EOF
$(grep -ho '<testsuite [^>]*' "$RESULTS_DIR"/*.xml | awk '
  {
    for (i = 1; i <= NF; i++) {
      if (split($i, kv, "=") == 2) {
        gsub(/"/, "", kv[2])
        val[kv[1]] = kv[2]
      }
    }
    tests    += val["tests"]
    failures += val["failures"]
    errors   += val["errors"]
    skipped  += val["skipped"]
    time     += val["time"]
    delete val
  }
  END { printf "%d %d %d %d %.3f", tests, failures, errors, skipped, time }
')
EOF

PASSED=$((TOTAL - FAILURES - ERRORS - SKIPPED))

if [ "$FAILURES" -eq 0 ] && [ "$ERRORS" -eq 0 ]; then
  STATUS="✅ Passed"
else
  STATUS="❌ Failed"
fi

{
  echo "### ${TITLE} — ${STATUS}"
  echo ""
  echo "| Total | Passed | Failed | Errors | Skipped | Time (s) |"
  echo "| ----- | ------ | ------ | ------ | ------- | -------- |"
  echo "| ${TOTAL} | ${PASSED} | ${FAILURES} | ${ERRORS} | ${SKIPPED} | ${TIME} |"
  echo ""
} >> "$SUMMARY_FILE"

# List failing test cases, if any, so failures are actionable from the summary.
if [ "$FAILURES" -ne 0 ] || [ "$ERRORS" -ne 0 ]; then
  {
    echo "<details><summary>Failing tests</summary>"
    echo ""
    # Extract "classname.name" for each testcase that contains a <failure>/<error>.
    awk '
      /<testcase / {
        line = $0
        cls = ""; nm = ""
        if (match(line, /classname="[^"]*"/)) { cls = substr(line, RSTART+11, RLENGTH-12) }
        if (match(line, / name="[^"]*"/))     { nm  = substr(line, RSTART+7, RLENGTH-8) }
        pending_cls = cls; pending_nm = nm; open = 1
        if (line ~ /\/>/) { open = 0 }
      }
      open && /<(failure|error)[ >]/ {
        printf "- `%s.%s`\n", pending_cls, pending_nm
        open = 0
      }
      /<\/testcase>/ { open = 0 }
    ' "$RESULTS_DIR"/*.xml
    echo ""
    echo "</details>"
    echo ""
  } >> "$SUMMARY_FILE"
fi
