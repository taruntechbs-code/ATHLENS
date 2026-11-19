# ATHLENS — Offline-First Native Android Sports-Tech Platform

ATHLENS is an offline-first native Android sports-tech platform engineered specifically for grassroots Kabaddi and Kho-Kho athletes, coaches, and scouts across Tamil Nadu and India. Built with Kotlin, Jetpack Compose, Material 3, and an Apple first-party **Liquid Glass / Contour Glass** design system, it provides on-device AI biomechanical coaching, maturity-aware talent assessment, cryptographically signed digital passports, and offline peer-to-peer talent exchange without requiring internet access or cloud infrastructure.

---

## 💎 Liquid Glass / Contour Glass Redesign (Phases 1–3 Complete)

The entire user interface has been elevated to an Apple first-party **Liquid Glass / Contour Glass** aesthetic, combining real-time GPU backdrop blur (`dev.chrisbanes.haze:haze:1.3.1`), multi-layered contour lighting, and real offline editorial photography:

### 1. Foundation & Design System (`LiquidGlass.kt`)
- **Real-Time Backdrop Blur**: `dev.chrisbanes.haze:haze:1.3.1` powers dynamic glass refraction across all cards, modals, top header, and navigation bars.
- **Contour Specular Lighting**: Every `ContourGlassCard` features a multi-stop linear gradient background, inner specular top-edge highlight (`1dp` stroke from white alpha to transparent), and dynamic outer borders (`GlassBorderEmerald`, `GlassBorderStandard`).
- **Ambient Mesh Backdrop (`AmbientGlassBackground.kt`)**: Deep obsidian canvas (`#050814`) layered with glowing midnight sapphire (`#0B132B`), electric emerald (`#10B981`), and ocean teal (`#0EA5E9`) radial gradients.
- **Floating Liquid Glass Navigation**:
  - **Top Bar**: Floating translucent capsule with ATHLENS glowing emerald brand mark and 3-way language switcher (`EN` ↔ `தமிழ்` ↔ `हिन्दी`).
  - **Bottom Navigation Pill (`FloatingGlassNavBar`)**: Floating Apple-style glass capsule with spring-animated icon scaling and active glass selection pills.

### 2. Editorial Athletic & Grassroots Photography (100% Offline in `drawable-nodpi/`)
- `img_train_hero.jpg`: Athletic preparation barbell grip for AI Camera Coach hero card.
- `img_drill_kabaddi.jpg`: Overhead barbell stability for Kabaddi Raid Stance drill card.
- `img_drill_khokho.jpg`: Deep crouch sprinting start on stadium track for Kho-Kho Crouch Start drill card.
- `img_passport_banner.jpg`: Floodlit stadium track backdrop for Apple Wallet–style Athlete Pass.
- `img_food_preworkout.jpg`: Ragi koozh pre-workout complex carbohydrates.
- `img_food_protein.jpg`: Poovan banana & roasted kadalai potassium and quick energy.
- `img_food_hydration.jpg`: Village neer mor traditional electrolyte hydration.
- `img_food_recovery.jpg`: Boiled black chana sundal & country eggs high-protein muscle recovery.

### 3. Apple Wallet–Style Athlete Pass (`PassportTab.kt`)
- **Wallet Pass Hero Card**: Scrimmed stadium photo backdrop, micro-label (`#shortId`), athlete name, FlowRow sport/position pills, and 3-tile tabular telemetry strip (Sessions, Form Score, Latest Drill).
- **Minor Guardian Legal Consent Card**: Distinct warm-amber glass shield confirming verified parental consent.
- **Cryptographic Talent Passport Card**: Displays active ECDSA P-256 Key Fingerprint and one-tap launch to the frosted QR modal dialog.
- **Maturity-Aware Assessment Card**: 2x2 glass metric grid (Exact Chronological Age, RAE Status, Growth Velocity cm/yr, Kinematic Progression Delta %) and Coach Advisory Banner.
- **Verified Achievements Card**: Glass medal shelf tracking district/state accolades.
- **Frosted QR Modal Dialog**: Apple-style frosted popup with high-density QR code, "Verify Integrity", and "Simulate Tamper" scout verification actions.

### 4. Liquid Hydration & Grassroots Nutrition (`NutritionTab.kt`)
- **Liquid Hydration Tracker**: Animated cyan-to-emerald gradient progress bar tracking daily water intake towards a 3,000 ml target with one-tap `+250 ml` log and reset actions.
- **Photo-Backed Meal Cards**: 84x84dp rounded food photography thumbnails with glowing emerald checkmark overlays upon completion.
- **Grassroots Economy Focus**: All meal options formulated for Tamil Nadu athletes using local staples under ₹30/day.

### 5. AirDrop-Style Decentralized Talent Exchange (`ShareP2PTab.kt`)
- **iOS-Style Glass Segmented Switcher**: Smooth toggle between `Send Passport` and `Receive (Scout)` modes.
- **Mini Wallet Preview**: Compact Apple-Wallet-style athlete pass summary before sharing.
- **Concentric Pulsing Radar Animation**: AirDrop-style radar waves actively visualizing Nearby Connections BLE & Wi-Fi Direct advertising.
- **Verified Scout Inbox**: Cryptographically validated athlete passes with genuine/tampered status badges and instant signature verification.
- **Cloud Sync Engine**: WorkManager offline telemetry queue status and manual "Sync Now" action.

### 6. Translucent Camera & Scanner HUDs (`CameraCoachScreen.kt` & `QrScannerScreen.kt`)
- Floating contour glass controls for lens flipping, drill selection, score telemetry, and real-time kinematic posture feedback banners without impacting 30 FPS MediaPipe computer vision throughput.

---

## 🌟 Complete 6-Phase Architectural Overview

### Phase 1: Zero-Cloud Encrypted Foundation & Child Safeguarding
- **Encrypted Local Room Database**: 100% offline SQLCipher integration (`net.zetetic:sqlcipher-android:4.6.1` + `androidx.sqlite:sqlite-ktx:2.4.0`).
- **Hardware-Backed Keystore Passphrase**: Generates a random 32-byte database encryption passphrase on first launch, encrypts it using an AES-256-GCM master key stored in the Android Keystore, and persists ciphertext + IV in standard `SharedPreferences`.
- **Grassroots Minor Athlete Consent Flow**: 3-step onboarding flow with mandatory parent/guardian legal authorization and consent when an athlete's age is calculated to be under 18.
- **4-Tab Sports-Tech Dashboard**:
  - `Train`: Athlete greeting, sport/position pill, "Offline Mode Ready" status badge, and AI Camera Coach launchpad.
  - `Passport`: Live Room `StateFlow` athlete passport display, minor guardian verification badge, and live training session telemetry aggregates.
  - `Nutrition`: Grassroots sports nutrition & recovery protocol tailored to local Indian food under ₹30/day.
  - `Share P2P`: Offline decentralized peer-to-peer Wi-Fi Direct & BLE mesh talent exchange.
- **Manual Dependency Injection**: Lightweight, compile-time safe `AppContainer` pattern in `AthlensApplication` (zero annotation/ksp conflicts, zero Hilt/Dagger bloat).

---

### Phase 2: On-Device AI Camera Coach & Biomechanics
- **CameraX 1.4.1**: Lifecycle-bound `ImageAnalysis` running `OUTPUT_IMAGE_FORMAT_RGBA_8888` at 30 FPS with front/back camera lens switching and proper sensor rotation/mirroring handling.
- **MediaPipe Tasks Vision 0.10.14**: On-device pose estimation running `pose_landmarker_lite.task` (~5.8 MB) in `RunningMode.LIVE_STREAM` with CPU delegate for crash-free compatibility across budget Android devices.
- **Pure Kotlin Kinematic Scoring Engine**:
  - **Kabaddi Raid Stance & Lunge (`KABADDI_RAID_STANCE`)**:
    - Ideal knee flexion: 95°–140°
    - Ideal forward torso lean: 15°–45°
    - Stable base width calculation
    - Real-time kinematic fault detection: `STANCE_TOO_HIGH`, `TORSO_TOO_UPRIGHT`, `UNSTABLE_BASE`, `GOOD_FORM`
  - **Kho-Kho Crouch Start (`KHO_KHO_CROUCH_START`)**:
    - Ideal deep crouch knee flexion: 60°–115°
    - Low hip center and compact forward readiness
    - Real-time kinematic fault detection: `HIPS_TOO_HIGH`, `KNEES_NOT_BENT`, `UNBALANCED_POSTURE`, `GOOD_FORM`
  - **Temporal Smoothing**: 8-frame rolling average for form score (`0–100`) preventing HUD flickering.
- **Real-Time Skeletal Overlay Canvas**:
  - Maps 33 MediaPipe pose landmarks and 35 bone connections dynamically scaled to CameraX `FILL_CENTER` aspect ratio.
  - Dynamic color coding: Electric Emerald (`#10B981`) for score ≥ 75, Warm Amber (`#F59E0B`) for 50–74, Coral Red (`#EF4444`) for < 50.
- **Dual-HUD Interface**:
  - **Top HUD**: Lens switcher (front/back), drill quick selector, close button, live score pill (`0–100`).
  - **Bottom HUD**: Real-time coaching feedback banner, telemetry chips (Knee Angle°, Torso Lean°, Latency ms), session timer, and "Start Session" / "Stop & Save" action button.
- **Encrypted Session Persistence**: Stopping a session automatically computes the average form score, serializes recurring error codes to a JSON array, and writes a `TrainingSessionEntity` into the encrypted SQLCipher Room database.

---

### Phase 3: Offline-First Voice Coaching (Android TextToSpeech)
- **Android 11–15 TTS Manifest Visibility**: Declared `<queries>` targeting `android.intent.action.TTS_SERVICE` in `AndroidManifest.xml` to ensure reliable system TTS engine binding across Android 11 through 15.
- **`VoiceCoachManager` Engine (`data/audio/VoiceCoachManager.kt`)**:
  - Debounced smart coaching with a 3.2-second cooldown on repeated error codes.
  - Milestone voice announcements on drill start and drill completion.
  - One-tap Mute/Unmute audio control in the top HUD.

---

### Phase 4: Trilingual Upgrade, Maturity Engine & Nutrition
- **English Default + Trilingual Support (`en` English, `ta` தமிழ், `hi` हिन्दी)**:
  - Default language set to English (`Language.ENGLISH` / `"en"`).
  - 3-way language selector on onboarding pre-selected to English: `English` | `தமிழ்` | `हिन्दी`.
  - Seamless 3-way cycle (`EN` -> `தமிழ்` -> `हिन्दी` -> `EN`) on Dashboard and Camera Coach HUD.
  - Trilingual voice coaching in `VoiceCoachManager` supporting `en-IN`, `ta-IN`, and `hi-IN` with multi-tier fallback.
- **Maturity-Aware Talent Assessment Engine**:
  - **Database Migration**: Added `GrowthMeasurementEntity` (height in cm, weight in kg, timestamp) and bumped Room to version 2.
  - **`MaturityAssessmentEngine`**:
    - Calculates exact chronological age (`XX yrs YY mos`).
    - Relative Age Effect (RAE) Birth Quarter (`Q1` to `Q4`) with late-born athlete indicator for `Q3`/`Q4` peers.
    - Growth Velocity calculation (`cm/year`) with alert for rapid growth spurts (> 7.5 cm/yr) where adolescent awkwardness occurs.
    - Form Progression Delta comparing baseline vs. recent drill kinematics.
    - Developmental scout context disclaimer.
  - **PassportTab Integration**: Interactive card with "Log Height / Weight" dialog saving directly into encrypted Room DB.
- **Offline Affordable Sports Nutrition & Recovery**:
  - Grassroots athlete meal & recovery plans under ₹30/day (Ragi Koozh, Poovan Banana + Kadalai, Black Chana Sundal, Country Eggs / Sprouted Moong, Village Neer Mor, Traditional Sathumaavu).
  - **Interactive Daily Hydration Tracker**: Tap `+250 ml` towards a 3,000 ml daily target, persisted in `SharedPreferences` per calendar day.
  - Interactive daily meal checklists with trilingual labels and educational disclaimer.

---

### Phase 5: Cryptographically Signed Talent Passport (ECDSA P-256) & QR Generator
- **Hardware-Backed Asymmetric Key Pair (`AndroidKeyStore`)**:
  - Alias: `ATHLENS_PASSPORT_KEY_ALIAS`.
  - Algorithm: `KeyProperties.KEY_ALGORITHM_EC` with `ECGenParameterSpec("secp256r1")`.
  - Purpose: `PURPOSE_SIGN` | `PURPOSE_VERIFY`, `DIGEST_SHA256`.
- **Deterministic Canonical Payload (`PassportSerializer`)**:
  - Lexicographically sorted keys: `athleteId`, `name`, `sport`, `position`, `district`, `ageMonths`, `raeQuarter`, `totalSessions`, `avgFormScore`, `verifiedAchievementsCount`, `issuedAtEpochSec`.
- **Offline Digital Signing & Verification (`PassportCryptoManager`)**:
  - Produces base64-encoded `SHA256withECDSA` digital signatures and DER public keys completely on-device.
  - 12-character SHA-256 Public Key Fingerprint for quick human-readable visual verification.
- **Room Database Upgrade (v3) — Achievements**:
  - Added `AchievementEntity` and `AchievementDao` tracking school, district, and state athletic accolades.
- **Offline QR Code Generator (`com.google.zxing:core:3.5.3`)**:
  - Encodes the signed passport JSON into a high-density, error-corrected QR code bitmap rendered in Jetpack Compose.
- **Interactive Verification & Tamper Check**:
  - Demonstrates cryptographic tamper-proofing by verifying live signatures and simulating payload alterations.

---

### Phase 6: Offline P2P Nearby Exchange, In-App QR Scanner & Cloud Sync
- **Offline P2P Transfer (`Google Nearby Connections API 19.3.0`)**:
  - Implements `Strategy.P2P_POINT_TO_POINT` (`SERVICE_ID = "com.athlens.app.P2P_EXCHANGE"`).
  - High-bandwidth zero-cloud peer-to-peer exchange utilizing BLE discovery and upgrading automatically to Wi-Fi Direct/Hotspot.
  - Zero internet, SIM card, or router required.
- **In-App Offline QR Scanner (`QrScannerScreen`)**:
  - CameraX `ImageAnalysis` bound to ZXing `MultiFormatReader` luminance analyzer.
  - Instantly decodes signed QR passports, verifies the hardware-backed ECDSA signature offline, and extracts the public key fingerprint.
- **Verified Scout Inbox (Room Database v4)**:
  - Added `ReceivedPassportEntity` and `ReceivedPassportDao` with unsynced tracking.
  - Displays verified scout inbox with genuine/tampered status badges, athletic stats, and transfer method (`P2P_NEARBY` vs `QR_SCAN`).
- **Offline-to-Cloud Sync Engine (`AndroidX WorkManager 2.10.0`)**:
  - `AthlensSyncWorker`: `CoroutineWorker` scheduled with `NetworkType.CONNECTED` constraint.
  - Queues unsynced training sessions and scout passports offline and automatically syncs when internet connectivity is restored.
  - Features a live sync status bar with pending item count and manual "Sync Now" trigger.
- **Sleek Trilingual Exchange UI (`ShareP2PTab`)**:
  - Mode switcher: `Send Passport` | `Receive (Scout)`.
  - Minor guardian consent check before athlete broadcast.
  - Nearby advertising radar animation and active device discovery list.

---

## 🛠 Technical Stack & System Specifications

| Layer | Technology |
|---|---|
| **OS & SDKs** | Android minSdk 26, targetSdk 35, compileSdk 35, JVM Target 17 |
| **Language & UI** | Kotlin 2.0.21, Jetpack Compose, Material 3 (BOM 2024.11.00), Navigation Compose 2.8.4, Haze 1.3.1 (Liquid Glass) |
| **Computer Vision** | Google MediaPipe Tasks Vision 0.10.14 (`pose_landmarker_lite.task`), CameraX 1.4.1 |
| **Audio & TTS** | Android TextToSpeech (`en-IN`, `ta-IN`, `hi-IN`) with `TTS_SERVICE` query visibility |
| **Encrypted Database** | Room 2.6.1 (v4) with KSP 2.0.21-1.0.28, SQLCipher 4.6.1, Android Keystore AES-256-GCM |
| **Cryptography** | Android Keystore ECDSA P-256 (`secp256r1`), SHA-256 with ECDSA, SHA-256 Key Fingerprinting |
| **QR Code Engine** | ZXing Core 3.5.3 (Offline QR generation + CameraX luminance scanning) |
| **P2P Networking** | Google Play Services Nearby Connections 19.3.0 (`P2P_POINT_TO_POINT`) |
| **Background Sync** | AndroidX WorkManager 2.10.0 (`work-runtime-ktx`) |
| **Architecture** | Clean MVVM, Manual Dependency Injection via `AppContainer` (zero Hilt/Dagger) |
| **Package ID** | `com.athlens.app` (`com.athlens.app.debug` for debug builds) |

---

## 📁 Project Architecture & Directory Structure

```
ATHLENS/
├── app/
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── assets/
│   │   │   └── pose_landmarker_lite.task (5.8 MB)
│   │   └── java/com/athlens/app/
│   │       ├── AthlensApplication.kt
│   │       ├── MainActivity.kt
│   │       ├── data/
│   │       │   ├── audio/
│   │       │   │   └── VoiceCoachManager.kt
│   │       │   ├── local/
│   │       │   │   ├── AthlensDatabase.kt (Room v4)
│   │       │   │   ├── dao/
│   │       │   │   │   ├── AchievementDao.kt
│   │       │   │   │   ├── AthleteProfileDao.kt
│   │       │   │   │   ├── GrowthMeasurementDao.kt
│   │       │   │   │   ├── ReceivedPassportDao.kt
│   │       │   │   │   └── TrainingSessionDao.kt
│   │       │   │   └── entity/
│   │       │   │       ├── AchievementEntity.kt
│   │       │   │       ├── AthleteProfileEntity.kt
│   │       │   │       ├── GrowthMeasurementEntity.kt
│   │       │   │       ├── ReceivedPassportEntity.kt
│   │       │   │       └── TrainingSessionEntity.kt
│   │       │   ├── p2p/
│   │       │   │   └── NearbyExchangeManager.kt
│   │       │   ├── repository/
│   │       │   │   ├── AthleteRepository.kt & AthleteRepositoryImpl.kt
│   │       │   │   └── TrainingRepository.kt & TrainingRepositoryImpl.kt
│   │       │   ├── security/
│   │       │   │   ├── KeystoreHelper.kt (AES-256-GCM)
│   │       │   │   └── PassportCryptoManager.kt (ECDSA P-256)
│   │       │   ├── sync/
│   │       │   │   └── AthlensSyncWorker.kt (WorkManager)
│   │       │   └── vision/
│   │       │       ├── KinematicScoringEngine.kt
│   │       │       ├── PoseLandmarkerHelper.kt
│   │       │       └── PoseOverlayView.kt
│   │       ├── di/
│   │       │   ├── AppContainer.kt
│   │       │   └── ViewModelFactory.kt
│   │       ├── domain/
│   │       │   ├── maturity/
│   │       │   │   └── MaturityAssessmentEngine.kt
│   │       │   ├── model/
│   │       │   │   ├── Achievement.kt
│   │       │   │   ├── AthleteProfile.kt
│   │       │   │   ├── DrillType.kt
│   │       │   │   ├── GrowthMeasurement.kt
│   │       │   │   ├── KinematicFeedback.kt
│   │       │   │   ├── Language.kt
│   │       │   │   ├── MaturityReport.kt
│   │       │   │   └── TrainingSession.kt
│   │       │   └── passport/
│   │       │       ├── PassportPayload.kt
│   │       │       ├── PassportQrGenerator.kt
│   │       │       └── PassportSerializer.kt
│   │       └── ui/
│   │           ├── coach/
│   │           │   ├── CameraCoachScreen.kt
│   │           │   └── CameraCoachViewModel.kt
│   │           ├── dashboard/
│   │           │   ├── DashboardScreen.kt
│   │           │   ├── DashboardUiState.kt
│   │           │   ├── DashboardViewModel.kt
│   │           │   └── tabs/
│   │           │       ├── NutritionTab.kt
│   │           │       ├── PassportTab.kt
│   │           │       ├── ShareP2PTab.kt
│   │           │       └── TrainTab.kt
│   │           ├── navigation/
│   │           │   ├── AthlensNavGraph.kt
│   │           │   └── Screen.kt
│   │           ├── onboarding/
│   │           │   ├── OnboardingScreen.kt
│   │           │   ├── OnboardingUiState.kt
│   │           │   └── OnboardingViewModel.kt
│   │           ├── p2p/
│   │           │   └── QrScannerScreen.kt
│   │           ├── theme/
│   │           │   ├── Color.kt
│   │           │   ├── Theme.kt
│   │           │   └── Type.kt
│   │           └── util/
│   │               └── BilingualStrings.kt
├── gradle/
│   └── libs.versions.toml
└── README.md
```

---

## 🚀 Build, Install & Device Deployment

### 1. Compile Debug APK
```powershell
$env:JAVA_HOME = "C:\Users\tarun\jdk-17\jdk-17.0.21+9"; .\gradlew.bat assembleDebug
```
Output:
```
BUILD SUCCESSFUL in 8s
37 actionable tasks: 11 executed, 26 up-to-date
```
Generated APK: `app/build/outputs/apk/debug/app-debug.apk`

### 2. Deploy to Physical Device via ADB
Ensure USB Debugging is enabled on your Android phone:
```powershell
adb devices
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb shell am start -n com.athlens.app.debug/com.athlens.app.MainActivity
```

### 3. Verify Logcat for Zero Crashes
```powershell
adb logcat -d -s AndroidRuntime:E
```
Confirms clean execution with zero fatal uncaught exceptions across all 6 phases.

---

## 🛡 Security, Child Privacy & Compliance
- **Data Minimization & Encryption**: Athlete biometric and personal data are strictly stored locally in SQLCipher AES-256 encrypted databases with Keystore-protected secrets.
- **Parental Authorization**: Explicit minor guardian consent is legally captured and immutably tagged to athlete profiles before enabling talent sharing.
- **Offline Cryptographic Trust**: Scouts verify athlete achievements and performance data through asymmetric ECDSA P-256 digital signatures that cannot be forged or tampered with even when completely disconnected from the internet.
