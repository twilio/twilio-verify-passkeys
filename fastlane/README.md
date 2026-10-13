fastlane documentation
----

# Installation

Make sure you have the latest version of the Xcode command line tools installed:

```sh
xcode-select --install
```

For _fastlane_ installation instructions, see [Installing _fastlane_](https://docs.fastlane.tools/#installing-fastlane)

# Available Actions

### assemble

```sh
[bundle exec] fastlane assemble
```

Assemble the SDK, the Android sample app and the instrumented test APK, and collect them in builds/

### lint

```sh
[bundle exec] fastlane lint
```

Check the Kotlin code style with ktlint

### detekt

```sh
[bundle exec] fastlane detekt
```

Run detekt

### android_unit_tests

```sh
[bundle exec] fastlane android_unit_tests
```

Run the Android unit tests with Kover coverage and enforce the coverage rule

### ios_unit_tests

```sh
[bundle exec] fastlane ios_unit_tests
```

Run the iOS unit tests on the arm64 simulator

### android_size_report

```sh
[bundle exec] fastlane android_size_report
```

Generate the Android SDK size report

### instrumented_tests

```sh
[bundle exec] fastlane instrumented_tests
```

Run the instrumented tests in Firebase Test Lab (gcloud must be authenticated)

### test_summary

```sh
[bundle exec] fastlane test_summary
```

Add a JUnit results table to the GitHub job summary. Options: title

### danger_check

```sh
[bundle exec] fastlane danger_check
```

Run a Danger check on the current PR. Options: id, dangerfile (a file in dangerfiles/)

### android_aars

```sh
[bundle exec] fastlane android_aars
```

Rename the AARs collected by assemble to TwilioPasskeys(-debug).aar

### xcframework

```sh
[bundle exec] fastlane xcframework
```

Build the debug and release XCFrameworks as debugFramework.zip and releaseFramework.zip

### android_sample_app

```sh
[bundle exec] fastlane android_sample_app
```

Build the Android sample app against the local SDK as sample-app.apk / sample-app.zip. Options: distribute

### prepare_release

```sh
[bundle exec] fastlane prepare_release
```

Bump the version and changelog of every platform with releasable commits, and commit them on a release/<versions> branch

### release_status

```sh
[bundle exec] fastlane release_status
```

Report each platform's version, and whether it's tagged (and for KMP, on Maven Central), to $GITHUB_OUTPUT

### publish_kmp

```sh
[bundle exec] fastlane publish_kmp
```

Publish the KMP SDK version in gradle.properties to Maven Central

### github_release

```sh
[bundle exec] fastlane github_release
```

Create this repo's GitHub release for a tag from its CHANGELOG.md entry. Options: tag

### publish_ios

```sh
[bundle exec] fastlane publish_ios
```

Release releaseFramework.zip from twilio-verify-passkeys-ios: update Package.swift, tag and create the release. Options: version

----


## iOS

### ios set_sample_backend

```sh
[bundle exec] fastlane ios set_sample_backend
```

Point the sample app at SAMPLE_BACKEND_URL (domain and webcredentials entitlement)

### ios size_report

```sh
[bundle exec] fastlane ios size_report
```

Build the sample app with and without the SDK and write the SDK's size impact to iosApp/build/sizes.txt

### ios distribute_sample_app

```sh
[bundle exec] fastlane ios distribute_sample_app
```

Distribute iosApp/IPAs/iosApp.ipa (built by size_report) with Firebase App Distribution

### ios simulator_app

```sh
[bundle exec] fastlane ios simulator_app
```

Build the sample app for the simulator as iosApp/sample-app.zip

----

This README.md is auto-generated and will be re-generated every time [_fastlane_](https://fastlane.tools) is run.

More information about _fastlane_ can be found on [fastlane.tools](https://fastlane.tools).

The documentation of _fastlane_ can be found on [docs.fastlane.tools](https://docs.fastlane.tools).
