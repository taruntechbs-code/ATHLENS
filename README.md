# ATHLENS — Offline-First Native Android MVP (Phases 1–4)

ATHLENS is an offline-first native Android sports-tech platform designed for grassroots Kabaddi and Kho-Kho athletes in Tamil Nadu and India.

## Phase 1 Overview
- **Encrypted Local Room DB**: Zero cloud reliance. Backed by SQLCipher (`net.zetetic:sqlcipher-android` + `androidx.sqlite:sqlite-ktx`).
- **Hardware-Backed Keystore**: A random 32-byte database encryption passphrase is generated on first launch, encrypted with AES-256-GCM via the Android Keystore, and persisted securely in standard `SharedPreferences`.
- **Grassroots Minor Athlete Consent Flow**: 3-step onboarding flow with mandatory parent/guardian legal authorization and consent when an athlete's age is calculated to be under 18.
- **4-Tab Sports-Tech Dashboard**:
  - `Train`: Athlete greeting, sport/position pill, "Offline Mode Ready" status badge, and AI Camera Coach launchpad.
  - `Passport`: Live Room `StateFlow` athlete passport display, minor guardian verification badge, and live training session telemetry aggregates.
  - `Nutrition`: Grassroots sports nutrition & recovery protocol tailored to local Indian food.
  - `Share P2P`: Offline decentralized peer-to-peer Wi-Fi Direct & BLE mesh talent exchange teaser for scouts and academy selectors.
- **Manual Dependency Injection**: Lightweight `AppContainer` pattern in `AthlensApplication` (no Hilt/Dagger).

---

## Phase 2 Overview: On-Device AI Camera Coach
- **CameraX 1.4.1**: Lifecycle-bound `ImageAnalysis` running `OUTPUT_IMAGE_FORMAT_RGBA_8888` at 30 FPS with front/back camera lens switching and proper rotation/mirroring handling.
- **MediaPipe Tasks Vision 0.10.14**: On-device pose estimation running `pose_landmarker_lite.task` in `RunningMode.LIVE_STREAM` with CPU delegate for crash-free compatibility across budget Android devices.
- **Pure Kotlin Kinematic Scoring Engine**:
  - **Kabaddi Raid Stance & Lunge (`KABADDI_RAID_STANCE`)**:
    - Ideal knee flexion: 95°–140°
    - Ideal forward torso lean: 15°–45°
    - Stable base width calculation
    - Error codes: `STANCE_TOO_HIGH`, `TORSO_TOO_UPRIGHT`, `UNSTABLE_BASE`, `GOOD_FORM`
  - **Kho-Kho Crouch Start (`KHO_KHO_CROUCH_START`)**:
    - Ideal deep crouch knee flexion: 60°–115°
    - Low hip center and compact forward readiness
    - Error codes: `HIPS_TOO_HIGH`, `KNEES_NOT_BENT`, `UNBALANCED_POSTURE`, `GOOD_FORM`
  - **Temporal Smoothing**: 8-frame rolling average for form score (`0–100`) preventing HUD flickering.
- **Real-Time Skeletal Overlay Canvas**:
  - Maps 33 MediaPipe pose landmarks and bone connections over CameraX `FILL_CENTER` aspect ratio.
  - Dynamic color coding: Electric Emerald (`#10B981`) for score ≥ 75, Warm Amber (`#F59E0B`) for 50–74, Coral Red (`#EF4444`) for < 50.
- **Dual-HUD Interface**:
  - **Top HUD**: Lens switcher (front/back), drill quick selector, close button, live score pill (`0–100`).
  - **Bottom HUD**: Real-time coaching feedback banner, telemetry chips (Knee Angle°, Torso Lean°, Inference Latency ms), session timer, and "Start Session" / "Stop & Save" action button.
- **Session Persistence**: Stopping a session automatically computes the average form score, serializes recurring error codes to a JSON array string, and writes a `TrainingSessionEntity` into the encrypted SQLCipher Room database.

---

## Phase 3 Overview: Offline-First Voice Coaching
- **Android 11–15 TTS Manifest Visibility**: Declared `<queries>` block targeting `android.intent.action.TTS_SERVICE` in `AndroidManifest.xml` to allow system TTS engine binding across Android 11 through 15.
- **`VoiceCoachManager` Engine (`data/audio/VoiceCoachManager.kt`)**:
  - Debounced smart coaching with a 3.2-second cooldown on repeated error codes.
  - Milestone voice announcements on drill start and drill completion.
  - One-tap Mute/Unmute audio control in the top HUD.

---

## Phase 4 Overview: Trilingual Upgrade, Maturity Engine & Nutrition
- **English Default + Trilingual Support (`en` English, `ta` தமிழ், `hi` हिन्दी)**:
  - Default language everywhere set to English (`Language.ENGLISH` / `"en"`).
  - 3-way language selector on onboarding pre-selected to English: `English` | `தமிழ்` | `हिन्दी`.
  - Seamless 3-way cycle (`EN` -> `தமிழ்` -> `हिन्दी` -> `EN`) on Dashboard and Camera Coach HUD.
  - Trilingual voice coaching in `VoiceCoachManager` supporting `en-IN`, `ta-IN`, and `hi-IN` with multi-tier fallback.
- **Phase 4A: Maturity-Aware Talent Assessment Engine**:
  - **Database Migration**: Added `GrowthMeasurementEntity` (height in cm, weight in kg, timestamp) and bumped Room to version 2 with destructive migration fallback.
  - **`MaturityAssessmentEngine`**:
    - Calculates exact chronological age (`XX yrs YY mos`).
    - Relative Age Effect (RAE) Birth Quarter (`Q1` to `Q4`) with late-born athlete indicator for `Q3`/`Q4` peers.
    - Growth Velocity calculation (`cm/year`) with alert for rapid growth spurts (> 7.5 cm/yr) where adolescent awkwardness occurs.
    - Form Progression Delta comparing baseline vs. recent drill kinematics.
    - Clear developmental coach context disclaimer.
  - **PassportTab Integration**: Interactive card with "Log Height / Weight" dialog saving directly into encrypted Room DB.
- **Phase 4B: Offline Affordable Sports Nutrition & Recovery**:
  - Grassroots athlete meal & recovery plans under ₹30/day (Ragi Koozh, Poovan Banana + Kadalai, Black Chana Sundal, Country Eggs / Sprouted Moong, Village Neer Mor, Traditional Sathumaavu).
  - **Interactive Daily Hydration Tracker**: Tap `+250 ml` towards a 3,000 ml daily target, persisted in `SharedPreferences` per calendar day.
  - Interactive daily meal checklists with trilingual labels and educational disclaimer.

---

## Technical Specifications
- **Target OS / SDKs**: `minSdk = 26`, `targetSdk = 35`, `compileSdk = 35`, `JVM Target = 17`
- **Language & Framework**: Kotlin 2.0.21, Jetpack Compose with Kotlin Compose Compiler Plugin, Material 3 (BOM 2024.11.00), Navigation Compose 2.8.4
- **Vision & AI**: MediaPipe Tasks Vision 0.10.14 (`pose_landmarker_lite.task`), CameraX 1.4.1
- **Audio & TTS**: Android TextToSpeech (`en-IN`, `ta-IN`, `hi-IN`) with `android.intent.action.TTS_SERVICE` query visibility
- **Database**: Room 2.6.1 (v2) with KSP 2.0.21-1.0.28, SQLCipher 4.6.1
- **Package**: `com.athlens.app` (`com.athlens.app.debug` for debug builds)

---

## Build & Device Run Verification
1. **Build debug APK**:
```powershell
$env:JAVA_HOME = "C:\Users\tarun\jdk-17\jdk-17.0.21+9"; .\gradlew.bat assembleDebug
```
Output:
```
BUILD SUCCESSFUL in 3s
37 actionable tasks: 5 executed, 32 up-to-date
```
Generated debug APK: `app/build/outputs/apk/debug/app-debug.apk`

2. **Install and Launch on Physical Device via ADB**:
```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.athlens.app.debug/com.athlens.app.MainActivity
```
Output:
```
Performing Streamed Install
Success
Starting: Intent { cmp=com.athlens.app.debug/com.athlens.app.MainActivity }
```
