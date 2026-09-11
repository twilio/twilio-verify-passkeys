#!/bin/bash

SAMPLE_BACKEND_DOMAIN="${SAMPLE_BACKEND_URL/https:\/\/}"
SAMPLE_BACKEND_DOMAIN="${SAMPLE_BACKEND_DOMAIN/\/}"
cd iosApp
sed -i '' "s|let domain: String = \".*\"|let domain: String = \"${SAMPLE_BACKEND_DOMAIN}\"|" iosApp/Constants.swift
sed -i '' "s|<string>webcredentials:.*</string>|<string>webcredentials:${SAMPLE_BACKEND_DOMAIN}</string>|" iosApp/iosApp.entitlements
