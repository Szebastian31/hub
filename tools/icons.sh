#!/bin/bash
# Copies each app's own icon into docs/icons/ for the web version, from the app repos next to this one
# (~/Code/family-key/hora, …). An app whose icon isn't found shows its first letter in a circle instead.
cd "$(dirname "$0")/.."
mkdir -p docs/icons
pick() { # name, then candidate paths inside ../<repo>
  local name=$1 repo=$2; shift 2
  for p in "$@"; do
    if [ -f "../$repo/$p" ]; then cp "../$repo/$p" "docs/icons/$name.png"; echo "  $name  ← $repo/$p"; return; fi
  done
  # one put in docs/icons/ by hand (Consensus Weather has no icon file in its repo) stays
  if [ -f "docs/icons/$name.png" ]; then echo "  $name  ← docs/icons/$name.png, as before"; return; fi
  echo "  $name  not found (shows a letter)"
}
pick hora hora icons/icon-192.png icons/icon-512.png
pick consensus Consensus_Weather docs/icon-192.png docs/icons/icon-192.png app/src/main/res/mipmap-xxxhdpi/ic_launcher.png app/src/main/res/mipmap-xxhdpi/ic_launcher.png
pick augur augur icon-192.png icons/icon-192.png
pick annales annales icons/icon-192.png docs/icons/icon-192.png
pick kalendae kalendae icons/icon-192.png
pick caelum caelum icons/icon-192.png
pick fidelis fidelis docs/icons/icon-192.png icons/icon-192.png
