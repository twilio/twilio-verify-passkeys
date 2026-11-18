#!/bin/bash

# Configure an SSH deploy key so git can push over SSH (used by the iOS release
# which pushes to the twilio-verify-passkeys-ios repo). Requires SSH_PRIVATE_KEY.

: "${SSH_PRIVATE_KEY:?Must set SSH_PRIVATE_KEY}"

mkdir -p "$HOME/.ssh"
chmod 700 "$HOME/.ssh"
printf '%s\n' "$SSH_PRIVATE_KEY" > "$HOME/.ssh/deploy_key"
chmod 600 "$HOME/.ssh/deploy_key"
cat >> "$HOME/.ssh/config" <<'EOF'
Host github.com
  IdentityFile ~/.ssh/deploy_key
  StrictHostKeyChecking accept-new
EOF
