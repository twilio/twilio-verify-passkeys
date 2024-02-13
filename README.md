# Twilio Verify Passkeys Android & iOS SDKs (KMP)

## Overview

Twilio Passkeys SDK enables developers to easily add Passkeys into their existing authentication flows within their own mobile applications. The Verify Passkeys SDK supports passkeys creation and authentication using the FIDO/WebAuthn industry standard. [Verify Passkeys Overview](https://www.twilio.com/docs/verify/passkeys)

## Project Structure

- `shared`: This module contains the shared code, including business logic, data models, and utility functions.
- `androidApp`: This module is specific to the Android platform and includes the Android sample app code.
- `iosApp`: This module is specific to the iOS platform and includes the iOS sample app code.

## Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) for Android development. Minimum version Hedgehog
- [Xcode](https://developer.apple.com/xcode/) for iOS development. 15.x
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) or [Android Studio](https://developer.android.com/studio) for shared code development.
- Android 9 (API Level 28) or higher
- iOS 16 or higher
- Gradle 8.2
- Java 17

### Building and Running

#### Android

1. Clone this repository.
2. Open the project in IntelliJ IDEA or Android Studio.
3. Set your backend URL [BaseUrl](https://github.com/twilio/passkey_kmm_demo/blob/main/androidApp/src/main/java/com/twilio/passkeys/android/di/TwilioPasskeyModule.kt#L42).
4. Build and run the Android app from the `androidApp` module.
5. To start sign up/in flows, the Android device must have a valid Google account to store and fetch passkeys.

#### iOS

1. Clone this repository.
2. Open the project in IntelliJ IDEA or Android Studio or open `iosApp` module in Xcode.
3. Set your backend URL [BaseUrl](https://github.com/twilio/passkey_kmm_demo/blob/main/iosApp/iosApp/Core/AuthenticationManager.swift#L22) and [Entitlements](https://github.com/twilio/passkey_kmm_demo/blob/main/iosApp/iosApp/iosApp.entitlements#L7)
4. Build and run the iOS app from the `iosApp` module.

## Code Structure

### Shared Code

The `shared` module contains code shared between Android and iOS. This includes:

- Data models
- Business logic
- Utility functions

### Android App

The `androidApp` module contains Android-specific code, such as:

- Sample application that works as a code snippet for integrating with the Twilio Verify Passkeys SDK
- Android-specific UI components

### iOS App

The `iosApp` module contains iOS-specific code, such as:

- Sample application that works as a code snippet for integrating with the Twilio Verify Passkeys SDK
- iOS-specific UI components

## Useful Gradle Tasks

### Running Unit Tests

#### Shared iOS Unit Tests

```
./gradlew :shared:iosSimulatorArm64Test
```

#### Shared Android Unit Tests

```
./gradlew :shared:testDebugUnitTest
```

To run tests with coverage report

```
./gradlew :shared:koverHtmlReportDebug
```

Code coverage rule only working on Android

```
./gradlew :shared:koverVerify
```

### Code rules

#### Ktlint Check
Check Ktlint rule violations

```
./gradlew ktlintCheck
```

#### Ktlint Format
Try to solve Ktlint rule violations

```
./gradlew ktlintFormat
```

#### Detekt Check
Check Detekt rule violations

```
./gradlew detekt
```
