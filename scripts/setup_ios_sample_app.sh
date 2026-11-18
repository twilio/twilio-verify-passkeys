#!/bin/bash

# Install Gem dependencies
./scripts/install_gem_dependencies.sh

# Configure GCloud App Distribution service
echo -n "$GCLOUD_SERVICE_KEY" | base64 --decode -o ${HOME}/gcloud-app-distribution-service-key.json
