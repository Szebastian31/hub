# Hub

**One list, seven apps: Hora, Consensus Weather, Augur, Annales, Kalendae, Caelum and Fidelis.**

Tap a row and that app opens. Each row shows the app's own icon, its name and its category. If an app isn't installed, its row is faded; it opens the web version where there is one (Annales and Fidelis are Android only).

## Getting started

1. Open **[Releases](https://github.com/Szebastian31/hub/releases/latest)** on your phone and download **Hub_v….apk**.
1. Open the file. If Android asks, allow your browser to **install unknown apps**.
1. Open Hub and tap any app.

Updating: download the new APK and install it over the old one.

---

For developers

A single native Activity with no WebView and no libraries. The rows are built in code in `MainActivity.java`. GitHub Actions builds the APK on every push and publishes it as a release.

| Where | What it is |
| --- | --- |
| `app/src/main/java/com/szebastian/hub/MainActivity.java` | The list screen. The `APPS` list at the top holds each app's package name, name, category and web address |
| `app/src/main/AndroidManifest.xml` | The `<queries>` list, which Android 11+ needs before one app can see and open another |
| `app/src/main/res/` | Theme colours and the launcher icon |
| `app/src/main/assets/fonts/` | Noto Serif, bundled so the phone's theme font can't replace it |
| `.github/workflows/build.yml` | Builds the APK and publishes it as a release |

**Signing.** Builds are signed with the family key. It never lives in git: GitHub builds read it from the `FAMILY_*` secrets, local builds from `~/.gradle/gradle.properties`. Every build installs over the last one.

**Adding an app:** add a line to `APPS` in `MainActivity.java` and a `<package>` line to `<queries>` in `AndroidManifest.xml`. Then bump `versionName` + `versionCode` in `app/build.gradle` for a new APK release.

## License

Released under the [MIT License](LICENSE).
