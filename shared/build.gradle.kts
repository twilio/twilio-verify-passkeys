plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinSerialization)
  alias(libs.plugins.kover)
  id("com.twilio.apkscale")
  id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
  id("co.touchlab.skie") version "0.6.1"
}

kotlin {
  applyDefaultHierarchyTemplate()

  androidTarget {
    compilations.all {
      kotlinOptions {
        jvmTarget = "17"
      }
    }
  }

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach {
    it.binaries.framework {
      baseName = "TwilioPasskeys"
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        // put your multiplatform dependencies here
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlin.coroutines.core)
      }
    }
    val commonTest by getting {
      dependencies {
        implementation(libs.kotlin.test)
        implementation(libs.kotlin.coroutines.test)
      }
    }
  }

  multiplatformSwiftPackage {
    packageName("PasskeyTestIOS")
    swiftToolsVersion("5.3")
    targetPlatforms {
      iOS { v("13") }
    }
    outputDirectory(File(rootDir, "/"))
  }
}

koverReport {
  filters {
    excludes {
      classes("*TestActivity")
    }
  }
  defaults {
    mergeWith("debug")
  }
  verify {
    rule {
      entity = kotlinx.kover.gradle.plugin.dsl.GroupingEntityType.APPLICATION
      bound {
        // lower bound
        minValue = 85

        // upper bound
        maxValue = 100

        // specify which units to measure coverage for
        metric = kotlinx.kover.gradle.plugin.dsl.MetricType.INSTRUCTION

        // specify an aggregating function to obtain a single value that will be checked against the lower and upper boundaries
        aggregation = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
      }
    }
  }
}

android {
  namespace = "com.twilio.passkeys"
  compileSdk = 34
  defaultConfig {
    minSdk = 24
    targetSdkVersion(34) // Don't remove this, apkscale plugin needs it
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
      excludes += "/META-INF/LICENSE.md"
      excludes += "/META-INF/LICENSE-notice.md"
    }
  }

  apkscale {
    abis = setOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
  }

  task("generateSizeReport") {
    dependsOn("assembleRelease", "measureSize")
    description = "Calculate Passkeys Android SDK Size Impact"
    group = "Reporting"

    doLast {
      var sizeReport =
        "### Size impact\n" +
          "\n" +
          "| ABI             | APK Size Impact |\n" +
          "| --------------- | --------------- |\n"
      val apkscaleOutputFile = file("$buildDir/apkscale/build/outputs/reports/apkscale.json")
      val jsonSlurper = groovy.json.JsonSlurper()
      val apkscaleOutput = jsonSlurper.parseText(apkscaleOutputFile.readText()) as List<*>
      val releaseOutput = apkscaleOutput[0] as Map<*, *>
      val sizes = releaseOutput["size"] as Map<String, String>
      sizes.forEach { (arch, sizeImpact) ->
        sizeReport += "| ${arch.padEnd(16)}| ${sizeImpact.padEnd(16)}|\n"
      }
      val sizeReportDir = "$buildDir/outputs/sizeReport"
      mkdir(sizeReportDir)
      val targetFile = file("$sizeReportDir/AndroidSDKSizeReport.txt")
      targetFile.createNewFile()
      targetFile.writeText(sizeReport)
    }
  }

  dependencies {
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.auth)
    testImplementation(libs.mockk.android)
    testImplementation(libs.robolectric)
    testImplementation(libs.truth)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.androidx.core.ktx)
    androidTestImplementation(libs.kotlin.test)
    androidTestImplementation(libs.kotlin.coroutines.test)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.junit.ktx)
    androidTestImplementation(libs.androidx.activity.ktx)
  }
}
