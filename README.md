# Twilio Verify Passkeys Android & iOS SDKs (KMP)

## Table of Contents

* [About](#about)
* [Documentation](#documentation)
* [Requirements](#requirements)
* [Installation](#installation)
* [Quickstart](#quickstart)
* [Building and Running Sample App](#building-and-running-sample-app)
* [Project Structure](#project-structure)
* [Code Structure](#code-structure)
* [Useful Gradle Tasks](#useful-gradle-tasks)

## About <a name="about"></a>

Twilio Passkeys SDK enables developers to easily add Passkeys into their existing authentication flows within their own mobile applications. The Verify Passkeys SDK supports passkeys creation and authentication using the FIDO/WebAuthn industry standard.

## Documentation <a name="documentation"></a>

[Verify Passkeys Overview](https://www.twilio.com/docs/verify/passkeys)

## Requirements <a name="requirements"></a>

- [Android Studio](https://developer.android.com/studio) for Android development. Minimum version Hedgehog
- [Xcode](https://developer.apple.com/xcode/) for iOS development. 15.x
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) or [Android Studio](https://developer.android.com/studio) for shared code development.
- Android 9 (API Level 28) or higher
- iOS 16 or higher
- Gradle 8.2
- Java 17

## Installation <a name="installation"></a>

### Installation Android
1. Download the .aar file from the [release page](https://github.com/twilio/twilio-verify-passkeys/releases).
2. Create a folder `libs` in the module directory.
3. Copy/move the .aar file in `libs` folder.
4. Add the implementation statement in dependencies:
```
implementation(files("libs/TwilioPasskeys.aar"))
```
5. Sync the project.
6. Use the SDK by creating an instance of TwilioPasskey:
```
val twilioPasskey = TwilioPasskey(context)
```

### Installation iOS

1. Download the XCFramework form the [release page](https://github.com/twilio/twilio-verify-passkeys/releases).
2. Create a Framework folder or use any name of your preference.
3. Copy/Move the XCFramework into the folder created at the previous step.
4. On your Project Configurations > General > Frameworks, Libraries, and Embedded Content section, drag & drop the XCFramework.
5. Import TwilioPasskeys in the files you will make use of it:
```
let twilioPasskey = TwilioPasskey()
```

## Quickstart <a name="quickstart"></a>

### Create registration

Use the `TwilioPasskey` instance to create a registration by calling the `create(String, AppContext)` function.

The first param is a `String` representation of a challenge payload, check how to [create your challenge payload](#create-challenge-payload) (`challengePayload`).

The second param is an instance of a `com.twilio.passkeys.AppContext`, it is created by passing the current `Activity` instance in Android or the `UIWindow` instance in iOS.

You can also call the `create(CreatePasskeyRequest, AppContext)` function, where `CreatePasskeyRequest` is a wrapper object of a [creation challenge payload](#create-challenge-payload) schema.

**Android**
```
val createPasskeyResult = twilioPasskey.create(challengePayload, AppContext(activity))
when(createPasskeyResult) {
  is CreatePasskeyResult.Success -> {
    // verify the createPasskeyResult.createPasskeyResponse against your backend and finish sign up
  }
  
  is  CreatePasskeyResult.Error -> {
    // handle error
  }
}
```

**iOS**

```
let response = try await twilioPasskey.create(challengePayload: challengePayload, appContext: AppContext(uiWindow: window))
if let success = response as? CreatePasskeyResult.Success {
  // verify the createPasskeyResult.createPasskeyResponse against your backend and finish sign up
} else if let error = response as? CreatePasskeyResult.Error {
  // handle error
}
```


### Authenticate a user

Use the `TwilioPasskey` instance to authenticate a user by calling the `authenticate(String, AppContext)` function.

The first param is a `String` representation of an authentication request, it follows the schema of an [authentication challenge payload](#authenticate-challenge-payload).

The second param is an instance of a `com.twilio.passkeys.AppContext`, it is created by passing the current `Activity` instance in Android or the `UIWindow` instance in iOS.

You can also call the `authenticate(AuthenticatePasskeyRequest, AppContext)` function, which the `AuthenticatePasskeyRequest` is a wrapper object of an [authentication challenge payload](#authenticate-challenge-payload).

**Android**

```
val authenticatePasskeyResult = twilioPasskey.authenticate(challengePayload, AppContext(activity))
when(authenticatePasskeyResult) {
  is AuthenticatePasskeyResult.Success -> {
    // verify the authenticatePasskeyResult.authenticatePasskeyResponse against your backend
  }
  
  is AuthenticatePasskeyResult.Error -> {
    // handle error 
  }
}
```

**iOS**

```
let response = try await twilioPasskey.authenticate(challengePayload: json, appContext: AppContext(uiWindow: window))
if let success = response as? AuthenticatePasskeyResult.Success {
  // verify the authenticatePasskeyResult.authenticatePasskeyResponse against your backend and finish sign in.
} else if let error = response as? AuthenticatePasskeyResult.Error {
  // handle error
}
```

### Create Challenge Payload <a name="create-challenge-payload"></a>

The challenge payload for creating a registration is a String obtained by requesting your backend a challenge for registering a user, it uses the JSON schema:
```
{"rp":{"id":"your_backend","name":"PasskeySample"},"user":{"id":"WUV...5Ng","name":"1234567890","displayName":"1234567890"},"challenge":"WUY...jZQ","pubKeyCredParams":[{"type":"public-key","alg":-7}],"timeout":600000,"excludeCredentials":[],"authenticatorSelection":{"authenticatorAttachment":"platform","requireResidentKey":false,"residentKey":"preferred","userVerification":"preferred"},"attestation":"none"}
```

### Authenticate Challenge Payload <a name="authenticate-challenge-payload"></a>

The challenge payload for authenticating a user is a JSON with the schema:
```
{"publicKey":{"challenge":"WUM...2Mw","timeout":300000,"rpId":"your_backend","allowCredentials":[],"userVerification":"preferred"}}
```

## Building and Running Sample App <a name="building-and-running-sample-app"></a>

#### Android

1. Clone this repository.
2. Open the project in IntelliJ IDEA or Android Studio.
3. Set your backend URL [BaseUrl](https://github.com/twilio/twilio-verify-passkeys/blob/main/androidApp/src/main/java/com/twilio/passkeys/android/di/TwilioPasskeyModule.kt#L42).
4. Build and run the Android app from the `androidApp` module.

**Note**: To start sign up/in flows, the Android device must have a valid Google account to store and fetch passkeys.

**Backend-side configuration for Android Sample App**<br>

1. Make sure you already [added support for digital asset links](https://developer.android.com/training/sign-in/passkeys#add-support-dal) in your backend by checking whether an entry with the build sha256 value exists. You can generate a sha256 by running `./gradlew signingreport`.
2. Add the origin if you have not added it yet, following the [official documentation](https://developer.android.com/training/sign-in/passkeys#verify-origin).


#### iOS

1. Clone this repository.
2. Open the project in IntelliJ IDEA or Android Studio or open `iosApp` module in Xcode.
3. Set your backend URL [BaseUrl](https://github.com/twilio/twilio-verify-passkeys/blob/main/iosApp/iosApp/Core/AuthenticationManager.swift#L22) and [Entitlements](https://github.com/twilio/twilio-verify-passkeys/blob/main/iosApp/iosApp/iosApp.entitlements#L7)
4. Build and run the iOS app from the `iosApp` module.

**Note**: To start sign up/in flows, the iPhone must have a valid iCloud account to store and fetch passkeys.

## Project Structure <a name="project-structure"></a>

- `shared`: This module contains the shared code, including business logic, data models, and utility functions.
- `androidApp`: This module is specific to the Android platform and includes the Android sample app code.
- `iosApp`: This module is specific to the iOS platform and includes the iOS sample app code.

## Code Structure <a name="code-structure"></a>

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

## Useful Gradle Tasks <a name="useful-gradle-tasks"></a>

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
