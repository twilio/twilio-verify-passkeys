#!/bin/bash

# Initialize gcloud CLI to connect to Google Cloud
echo $GCLOUD_SERVICE_KEY | base64 --decode > ${HOME}/gcloud-service-key.json
gcloud auth activate-service-account --key-file=${HOME}/gcloud-service-key.json
gcloud --quiet config set project "$GOOGLE_PROJECT_ID"

# Run android instrumented tests in Firebase test lab - Device Pixel 9 Api 35
gcloud firebase test android run --app ftl/dummy.apk --test builds/shared-debug-androidTest.apk --device model=tokay,version=35,locale=en,orientation=portrait
