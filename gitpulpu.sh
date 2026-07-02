#!/bin/bash
# Gitpulpu launcher — shows notification immediately, then starts the app
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

notify-send -i "$HOME/.local/share/icons/hicolor/scalable/apps/gitpulpu.svg" \
    "Gitpulpu" "Starting..." -t 5000 2>/dev/null &

cd "$SCRIPT_DIR" && ./gradlew run --daemon 2>/dev/null
