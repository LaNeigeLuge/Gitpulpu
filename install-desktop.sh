#!/bin/bash
# Install Gitpulpu desktop launcher for Linux
#
# Usage:
#   ./install-desktop.sh              # Uses ./gradlew run as the launch command
#   ./install-desktop.sh /path/to.jar # Uses a specific JAR file
#
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
APP_NAME="gitpulpu"
DESKTOP_FILE="$HOME/.local/share/applications/${APP_NAME}.desktop"
ICON_DIR="$HOME/.local/share/icons/hicolor/scalable/apps"
ICON_FILE="${ICON_DIR}/${APP_NAME}.svg"

# Install icon
mkdir -p "$ICON_DIR"
cp "$SCRIPT_DIR/icons/logo.svg" "$ICON_FILE"
echo "Installed icon to $ICON_FILE"

# Determine exec command
if [ -n "${1:-}" ] && [ -f "$1" ]; then
    EXEC_CMD="java -jar $1"
else
    EXEC_CMD="bash -c 'cd $SCRIPT_DIR && JAVA_HOME=/usr/lib/jvm/java-21-openjdk ./gradlew run'"
fi

# Write desktop entry
cat > "$DESKTOP_FILE" <<EOF
[Desktop Entry]
Type=Application
Name=Gitpulpu
GenericName=Git Client
Comment=Multiplatform Git client with a warm editorial UI
Exec=$EXEC_CMD
Icon=$APP_NAME
Terminal=false
Categories=Development;RevisionControl;
Keywords=git;vcs;version;control;repository;
StartupWMClass=com-jetpackduba-gitnuro-MainKt
EOF

chmod +x "$DESKTOP_FILE"
echo "Installed desktop entry to $DESKTOP_FILE"

# Update desktop database
if command -v update-desktop-database &>/dev/null; then
    update-desktop-database "$HOME/.local/share/applications" 2>/dev/null || true
fi

echo ""
echo "Done! Gitpulpu should now appear in your application launcher."
echo "If it doesn't show up immediately, log out and back in."
