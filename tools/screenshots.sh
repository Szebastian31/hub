#!/bin/bash
# Makes the README banner from the real web version (docs/index.html), plus the web version's PNG icons.
# Run tools/icons.sh first so the rows show each app's own icon. Nothing is saved: your browser data isn't touched.
#
#   tools/screenshots.sh

cd "$(dirname "$0")/.."
CHROME="/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
[ -x "$CHROME" ] || { echo "Google Chrome isn't in /Applications. Install it, or change CHROME in this file."; exit 1; }
ROOT="$(pwd)"
mkdir -p screenshots docs/icons

shoot() { # url, file, width, height, pixel scale
  rm -f "$ROOT/$2"
  "$CHROME" --headless --disable-gpu --hide-scrollbars --no-first-run --no-default-browser-check --allow-file-access-from-files \
    --user-data-dir="$(mktemp -d)" --force-device-scale-factor="$5" --window-size="$3,$4" \
    --virtual-time-budget=5000 --screenshot="$ROOT/$2" "$1" >/dev/null 2>&1 || true
  if [ -s "$ROOT/$2" ]; then echo "  $2"; else echo "  $2 FAILED"; fi
}

echo "Icons:"
shoot "file://$ROOT/tools/icon.html" "docs/icons/icon-512.png" 512 512 1
shoot "file://$ROOT/tools/icon.html" "docs/icons/icon-180.png" 512 512 0.3515625

# the phone screen is 390 × 844 points at 3× (1170 × 2532 px, an iPhone-sized screenshot), via tools/phone.html
echo "Phone screen:"
shoot "file://$ROOT/tools/phone.html" "screenshots/hub.png" 780 1688 1.5

echo "Banner:"
shoot "file://$ROOT/tools/banner.html" "screenshots/banner.png" 1280 720 2
echo "Done. Open the folder with: open screenshots"
