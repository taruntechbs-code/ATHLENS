# ATHLENS — Offline-First Native Android MVP (Phases 1, 2 & 3)

ATHLENS is an offline-first native Android sports-tech platform designed for grassroots Kabaddi and Kho-Kho athletes in Tamil Nadu.

## Phase 1 Overview
- **Encrypted Local Room DB**: Zero cloud reliance. Backed by SQLCipher (`net.zetetic:sqlcipher-android` + `androidx.sqlite:sqlite-ktx`).
- **Hardware-Backed Keystore**: A random 32-byte database encryption passphrase is generated on first launch, encrypted with AES-256-GCM via the Android Keystore, and persisted securely in standard `SharedPreferences`.
- **Grassroots Minor Athlete Consent Flow**: 3-step onboarding flow supporting real bilingual labels (`தமிழ்` & `English`). Mandates parent/guardian legal authorization and consent when an athlete's age is calculated to be under 18.
- **4-Tab Sports-Tech Dashboard**:
  - `Train`: Athlete greeting, sport/position pill, "Offline Mode Ready" status badge, and AI Camera Coach launchpad.
  - `Passport`: Live Room `StateFlow` athlete passport display, minor guardian verification badge, and live training session telemetry aggregates.
  - `Nutrition`: Grassroots Tamil Nadu affordable nutrition & recovery protocol (Ragi Koozh, Boiled Sundal, Neer Mor).
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
  - **Bottom HUD**: Real-time bilingual coaching feedback banner (`தமிழ்` / `English`), telemetry chips (Knee Angle°, Torso Lean°, Inference Latency ms), session timer, and "Start Session" / "Stop & Save" action button.
- **Session Persistence**: Stopping a session automatically computes the average form score, serializes recurring error codes to a JSON array string, and writes a `TrainingSessionEntity` into the encrypted SQLCipher Room database.

---

## Phase 3 Overview: Offline-First Tamil & English Voice Coaching
- **Android 11–15 TTS Manifest Visibility**: Declared `<queries>` block targeting `android.intent.action.TTS_SERVICE` in `AndroidManifest.xml` to allow system TTS engine binding across Android 11 through 15.
- **`VoiceCoachManager` Engine (`data/audio/VoiceCoachManager.kt`)**:
  - **Bilingual TTS Support**: Auto-detects athlete profile language (`ta-IN` Tamil or `en-IN` English) with graceful fallback to `Locale("ta")` or `Locale.ENGLISH` if specific regional voice packs are uninstalled.
  - **Sports Audio Attributes**: Bound to `AudioAttributes.USAGE_ASSISTANCE_SONIFICATION` and `CONTENT_TYPE_SPEECH`.
  - **Debounced Smart Coaching**: Intelligent speech pacing with a 3.2-second cooldown on repeated error codes to prevent voice clipping or repetitive audio chatter while an athlete is holding a stance.
  - **Milestone Voice Announcements**: Verbal confirmation of drill start (*"பயிற்சி தொடங்குகிறது. தயாராக இருங்கள்."* / *"Session started. Ready, go!"*) and drill completion (*"பயிற்சி முடிந்தது. மதிப்பெண் {score}."* / *"Session completed! Form score is {score}."*).
- **HUD Voice Controls**:
  - **Mute/Unmute**: One-tap toggle button (`VolumeUp` / `VolumeOff`) in the top HUD.
  - **Language Switcher**: Instant `தமிழ்` / `EN` toggle button in the top HUD updating both spoken voice coaching and written text banners in real time.
  - **Voice Status Badge**: Visual feedback indicator in the bottom coaching card displaying active/muted status.
- **Physical Device Deployment**:
  - Verified and deployed on physical Android hardware (`OnePlus Nord CE4 / CPH2585`, Android 14) via ADB (`com.athlens.app.debug`).

---

## Technical Specifications
- **Target OS / SDKs**: `minSdk = 26`, `targetSdk = 35`, `compileSdk = 35`, `JVM Target = 17`
- **Language & Framework**: Kotlin 2.0.21, Jetpack Compose with Kotlin Compose Compiler Plugin, Material 3 (BOM 2024.11.00), Navigation Compose 2.8.4
- **Vision & AI**: MediaPipe Tasks Vision 0.10.14 (`pose_landmarker_lite.task`), CameraX 1.4.1
- **Audio & TTS**: Android TextToSpeech (`ta-IN`, `en-IN`) with `android.intent.action.TTS_SERVICE` query visibility
- **Database**: Room 2.6.1 with KSP 2.0.21-1.0.28, SQLCipher 4.6.1
- **Package**: `com.athlens.app` (`com.athlens.app.debug` for debug builds)

---

## Project Structure
```
ATHLENS/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml               # Permissions, TTS query declaration, Activity
│       ├── assets/
│       │   └── pose_landmarker_lite.task     # 5.78 MB on-device pose model
│       ├── java/com/athlens/app/
│       │   ├── AthlensApplication.kt         # Application container initialization
│       │   ├── MainActivity.kt               # Edge-to-edge Compose entrypoint
│       │   ├── data/
│       │   │   ├── audio/
│       │   │   │   └── VoiceCoachManager.kt   # Offline TTS coaching engine (ta-IN / en-IN)
│       │   │   ├── local/
│       │   │   │   ├── AthlensDatabase.kt         # Room database declaration
│       │   │   │   ├── AthlensDatabaseFactory.kt  # Safe SQLCipher native loading & helper factory
│       │   │   │   ├── dao/
│       │   │   │   │   ├── AthleteProfileDao.kt   # Profile CRUD & StateFlow queries
│       │   │   │   │   └── TrainingSessionDao.kt  # Training session queries
│       │   │   │   └── entity/
│       │   │   │       ├── AthleteProfileEntity.kt
│       │   │   │       └── TrainingSessionEntity.kt
│       │   │   ├── repository/
│       │   │   │   ├── AthleteRepository.kt
│       │   │   │   ├── AthleteRepositoryImpl.kt
│       │   │   │   ├── TrainingRepository.kt
│       │   │   │   └── TrainingRepositoryImpl.kt
│       │   │   ├── security/
│       │   │   │   └── PassphraseManager.kt       # AES-256-GCM Android Keystore passphrase engine
│       │   │   └── vision/
│       │   │       └── PoseLandmarkerHelper.kt    # MediaPipe LIVE_STREAM pose detection pipeline
│       │   ├── di/
│       │   │   ├── AppContainer.kt                # Manual AppContainer DI with VoiceCoachManager
│       │   │   └── ViewModelFactory.kt            # Lifecycle ViewModel provider
│       │   ├── domain/
│       │   │   ├── kinematics/
│       │   │   │   ├── DrillType.kt               # Kabaddi Raid Stance & Kho-Kho Crouch Start
│       │   │   │   ├── KinematicFeedback.kt       # Telemetry feedback model & error codes
│       │   │   │   └── KinematicAnalyzer.kt       # Angle calculation & 8-frame smoothing engine
│       │   │   └── model/
│       │   │       ├── AthleteProfile.kt          # Domain model with age calculation
│       │   │       ├── TrainingSession.kt         # Telemetry session model
│       │   │       ├── Role.kt                    # Athlete, Coach, Scout
│       │   │       ├── Sport.kt                   # Kabaddi, Kho-Kho
│       │   │       ├── PlayingPosition.kt         # Sport-specific positions
│       │   │       └── Language.kt                # Tamil (ta), English (en)
│       │   └── ui/
│       │       ├── coach/
│       │       │   ├── CameraCoachScreen.kt       # Full-screen CameraX + dual HUD + voice controls
│       │       │   ├── CameraCoachViewModel.kt    # Inference processing, Room persistence & TTS
│       │       │   └── PoseOverlayCanvas.kt       # Dynamic skeletal drawing canvas
│       │       ├── dashboard/
│       │       │   ├── DashboardScreen.kt         # 4-tab M3 shell with language toggle
│       │       │   ├── DashboardViewModel.kt      # Live state management
│       │       │   ├── DashboardTab.kt            # Tab enum & icons
│       │       │   └── tabs/
│       │       │       ├── TrainTab.kt            # Train greeting, AI coach launch, recent sessions
│       │       │       ├── PassportTab.kt         # Verified sports passport & live telemetry aggregates
│       │       │       ├── NutritionTab.kt        # TN affordable superfoods
│       │       │       └── ShareP2PTab.kt         # Offline mesh talent exchange
│       │       ├── navigation/
│       │       │   ├── AthlensNavGraph.kt         # Dynamic start destination routing
│       │       │   └── Screen.kt
│       │       ├── onboarding/
│       │       │   ├── OnboardingScreen.kt        # 3-step wizard with progress tracking
│       │       │   ├── OnboardingViewModel.kt     # Form validation & database submission
│       │       │   ├── OnboardingUiState.kt
│       │       │   └── components/
│       │       │       ├── Step1LanguageRole.kt   # Language & role selector
│       │       │       ├── Step2ProfileDetails.kt # Name, DOB picker, TN district, sport/position
│       │       │       └── Step3MinorConsent.kt   # Conditional guardian authorization
│       │       ├── theme/
│       │       │   ├── Color.kt                   # Sports-tech palette (#0B132B, #10B981, #F59E0B)
│       │       │   ├── Shape.kt
│       │       │   ├── Theme.kt
│       │       │   └── Type.kt
│       │       └── util/
│       │           ├── BilingualStrings.kt        # Real English & Tamil localization dictionary & voice cues
│       │           └── DateUtils.kt               # Epoch calculation & date formatters
│       └── res/                                   # Icons, values, strings, adaptive launcher
├── gradle/
│   ├── libs.versions.toml                         # Dependency catalog
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties              # Gradle 8.11.1
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── gradlew                                        # Executable wrapper (LF line endings)
└── gradlew.bat
```

---

## Build & Device Run Verification
1. **Build debug APK**:
```powershell
$env:JAVA_HOME = "C:\Users\tarun\jdk-17\jdk-17.0.21+9"; .\gradlew.bat assembleDebug
```
Output:
```
BUILD SUCCESSFUL in 10s
37 actionable tasks: 11 executed, 26 up-to-date
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
