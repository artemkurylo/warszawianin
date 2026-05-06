# LOCAL_DEV.md — Warszawianin Development Setup

## Prerequisites

| Tool | Version | Notes |
|------|---------|-------|
| JDK | 17 | Gradle 8.7 requires JDK 17–21. Project pins JDK 17 in `gradle.properties` |
| Android SDK | API 34 | `~/Library/Android/sdk` (macOS default) |
| Android Build Tools | 34+ | Installed via SDK Manager |
| Device / Emulator | Android 8+ (API 26) | Physical phone recommended for camera/GPS |

### macOS Quick Install (if missing)

```bash
# JDK 17 (Azul Zulu — free, works on Apple Silicon)
brew install --cask zulu17

# Android SDK — easiest via Android Studio, or manually:
# https://developer.android.com/studio
```

---

## Build the APK

```bash
cd warszawianin
./gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk` (~19 MB)

> ⚠️ If you get a JDK version error, ensure `gradle.properties` has:
> ```
> org.gradle.java.home=/Library/Java/JavaVirtualMachines/zulu-17.jdk/Contents/Home
> ```
> Adjust the path to your JDK 17 installation.

---

## Install on Android Phone

### Option A — ADB (USB cable)

1. **Enable Developer Options** on your phone:
   - Settings → About Phone → tap "Build Number" 7 times
2. **Enable USB Debugging**:
   - Settings → Developer Options → USB Debugging → ON
3. Connect phone via USB, approve the prompt on phone
4. Install:
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```
5. Open "Warszawianin" from app drawer

### Option B — ADB over WiFi (wireless)

1. Follow steps 1–2 above, plus enable **Wireless Debugging** (Android 11+)
2. On phone: Settings → Developer Options → Wireless Debugging → Pair with code
3. On Mac:
   ```bash
   adb pair <ip>:<port>    # use IP:port shown on phone
   adb connect <ip>:<port> # use the connection port (different from pair port)
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### Option C — Direct transfer (no ADB)

1. Send `app-debug.apk` to your phone (AirDrop won't work → use Telegram, email, Google Drive, etc.)
2. On phone: open the APK file
3. Allow "Install from unknown sources" when prompted
4. Install & open

---

## Run on Emulator

```bash
# List available emulators
emulator -list-avds

# Start one
emulator -avd <avd_name>

# Install
adb install app/build/outputs/apk/debug/app-debug.apk
```

Or just use Android Studio: open the project, hit ▶️ Run.

---

## Project Structure

```
warszawianin/
├── app/
│   ├── build.gradle.kts          # App-level build config + dependencies
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/pl/warszawianin/
│       │   ├── WarszawianinApp.kt           # Hilt Application
│       │   ├── MainActivity.kt             # Single Activity (Compose)
│       │   ├── navigation/
│       │   │   └── NavGraph.kt             # 3-screen navigation
│       │   └── ui/
│       │       ├── theme/                  # Material 3 theming
│       │       └── screens/
│       │           ├── ticketlist/          # Home — list of reports
│       │           ├── photocapture/       # Camera / gallery picker
│       │           └── reportdraft/        # AI draft + submit
│       └── res/
│           ├── mipmap-anydpi-v26/          # Adaptive icon
│           └── values/                     # Strings, colors, themes
├── build.gradle.kts              # Root build config
├── settings.gradle.kts
├── gradle.properties             # JVM args, JDK path
├── gradle/
│   ├── libs.versions.toml        # Version catalog (all deps)
│   └── wrapper/
├── gradlew / gradlew.bat         # Gradle wrapper
├── IDEA.md                       # Product spec
└── LOCAL_DEV.md                  # ← You are here
```

---

## Useful Commands

```bash
# Full clean build
./gradlew clean assembleDebug

# Install directly to connected device
./gradlew installDebug

# List connected devices
adb devices

# View app logs
adb logcat -s "Warszawianin"

# Uninstall from device
adb uninstall pl.warszawianin
```

---

## Notes

- `local.properties` is gitignored — each dev must have their own pointing to their Android SDK
- The project uses **JDK 17** pinned in `gradle.properties`. Change the path if your JDK 17 is elsewhere
- All screens are placeholder shells — navigation works, but no real logic yet
- Min SDK 26 (Android 8.0) — covers 95%+ of active devices
